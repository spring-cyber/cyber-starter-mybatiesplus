package com.cyber.mybatiesplus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增时填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        boolean createTime = metaObject.hasSetter("createTime");
        if (createTime) {
            this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
        updateFill(metaObject);

    }

    /**
     * 修改时填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateTime = getFieldValByName("updateTime", metaObject);
        if (updateTime == null) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }

    }
}
