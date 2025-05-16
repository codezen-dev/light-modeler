package com.sysml.lightmodel.semantic;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
@Data
public class AttributeDefinition extends Definition {
    private String type;
    private String defaultValue;
}