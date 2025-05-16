package com.sysml.lightmodel.semantic;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
@Data
public class ValueDefinition extends Definition {
    private String value;
}