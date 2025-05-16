package com.sysml.lightmodel.semantic;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
@Data
public class AttributeUsage extends Usage {
    private AttributeDefinition definition;
    private String value;
}