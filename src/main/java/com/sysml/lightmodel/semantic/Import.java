package com.sysml.lightmodel.semantic;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
@Data
public class Import extends Element {
    private Element importedElement;
}