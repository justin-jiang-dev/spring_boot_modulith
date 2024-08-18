package com.nuoson.modulith;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomZipCodeValidator implements ConstraintValidator<CustomZipCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 自身不需要任何验证逻辑，依赖组合中的约束进行验证即可
        return true;
    }

}