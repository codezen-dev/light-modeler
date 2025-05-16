package com.sysml.lightmodel.semantic;

import com.sysml.lightmodel.handler.ListToJsonTypeHandler;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
@Data
public class Package extends Element {
    @TableField(typeHandler = ListToJsonTypeHandler.class)
    private List<Element> ownedElements;
}