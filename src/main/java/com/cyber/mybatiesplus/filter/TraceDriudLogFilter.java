package com.cyber.mybatiesplus.filter;

import brave.Span;
import brave.propagation.ThreadLocalSpan;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;

public class TraceDriudLogFilter extends Slf4jLogFilter {

    private Logger TRACE = LoggerFactory.getLogger("trace");
    private Logger LOGGER = LoggerFactory.getLogger(TraceDriudLogFilter.class);

    @Override
    public boolean isStatementLogEnabled() {
        return true;
    }

    @Override
    public boolean isStatementLogErrorEnabled() {
        return true;
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        Span span = ThreadLocalSpan.CURRENT_TRACER.next();
        if (null != span) {
            span.start();
        }
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        try {
            super.statementExecuteAfter(statement, sql, firstResult);
        } finally {
            log(statement, sql);
        }
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        Span span = ThreadLocalSpan.CURRENT_TRACER.next();
        if (null != span) {
            span.start();
        }
        super.statementExecuteBatchBefore(statement);
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        try {
            super.statementExecuteBatchAfter(statement, result);
        } finally {
            String sql = statement instanceof PreparedStatementProxy ? ((PreparedStatementProxy) statement).getSql() : statement.getBatchSql();
            log(statement, sql);
        }

    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        Span span = ThreadLocalSpan.CURRENT_TRACER.next();
        if (null != span) {
            span.start();
        }
        super.statementExecuteQueryBefore(statement, sql);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        try {
            super.statementExecuteQueryAfter(statement, sql, resultSet);
        } finally {
            log(statement, sql);
        }

    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        Span span = ThreadLocalSpan.CURRENT_TRACER.next();
        if (null != span) {
            span.start();
        }
        super.statementExecuteUpdateBefore(statement, sql);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {

        try {
            super.statementExecuteUpdateAfter(statement, sql, updateCount);
        } finally {
            log(statement, sql);
        }

    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        try {
            super.statement_executeErrorAfter(statement, sql, error);
        } finally {
            log(statement, sql);
        }
    }

    private void log(StatementProxy statement, String rawSql) {
        try {
            String sqlParam = "";
            Span span = ThreadLocalSpan.CURRENT_TRACER.remove();
            if(span==null) {
                return;
            }
            if (statement.getParametersSize() != 0) {
                sqlParam = JSONObject.toJSONString(statement.getParameters());
            }
            String localIp = InetAddress.getLocalHost().getHostAddress();

            StringBuilder logBuilder = new StringBuilder();

            long total = 0, success = 0, error = 0;
            JSONObject resultStat = new JSONObject();

            JdbcSqlStat jdbcSqlStat = statement.getSqlStat();
            if (jdbcSqlStat != null) {
                total = statement.getSqlStat().getExecuteCount();
                success = statement.getSqlStat().getExecuteSuccessCount();
                error = statement.getSqlStat().getErrorCount();
            }
            resultStat.putIfAbsent("total", total);
            resultStat.putIfAbsent("success", success);
            resultStat.putIfAbsent("error", error);

            String result = total == success ? "0" : "1";
            String tRequestId = getValue("tRequestId");
            String xRequestId = getValue("X-Request-ID");
            String referer = getValue("Referer");
            String userAgent = getValue("User-Agent");
            String xB3TraceId = getValue("X-B3-TraceId");
            String xB3SpanId = getValue("X-B3-SpanId");
            String xB3ParentSpanId = getValue("X-B3-ParentSpanId");
            String xSpanExport = getValue("X-Span-Export");

            logBuilder.append("[")
                    .append("sql").append("|")
                    .append(statement.getLastExecuteType().name()).append("|")
                    .append(sqlParam).append("|")
                    .append(JSONObject.toJSONString(rawSql)).append("|")
                    .append(localIp).append("|")
                    .append(statement.getLastExecuteTimeNano()).append("|")
                    .append(result).append("|")
                    // 返回的字节数，暂时无法取出
                    .append("-").append("]")
                    .append("[")
                    .append(tRequestId).append("|")
                    .append(xRequestId).append("|")
                    .append(referer).append("|")
                    .append(userAgent).append("]")
                    .append("[")
                    .append(xB3TraceId).append("|")
                    .append(xB3SpanId).append("|")
                    .append(xB3ParentSpanId).append("|")
                    .append(xSpanExport).append("]")
                    .append("[")
                    .append(statement.getLastExecuteStartNano()).append("|")
                    .append((statement.getLastExecuteStartNano() + statement.getLastExecuteTimeNano())).append("|")
                    .append(JSONObject.toJSONString(resultStat))
                    .append("]");
            TRACE.info(logBuilder.toString());
            span.finish();
        } catch (Throwable throwable) {
            LOGGER.error("Druid Sql Trace Log Error ...", throwable);
        }
    }

    private String getValue(String key) {
        // if value's able to get from mdc ,then get it
        String mdc = MDC.get(key);
        return mdc == null ? "" : mdc;
    }
}
