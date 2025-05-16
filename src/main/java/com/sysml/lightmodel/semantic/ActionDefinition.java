package com.sysml.lightmodel.semantic;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.List;

@Data
public class ActionDefinition extends Definition {


    private List<Feature> parameters;

}