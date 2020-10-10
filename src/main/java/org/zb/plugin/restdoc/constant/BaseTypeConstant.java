package org.zb.plugin.restdoc.constant;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import org.zb.plugin.restdoc.definition.ConvertFieldType;
import org.zb.plugin.restdoc.parser.translator.TypeTranslator;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BaseTypeConstant {

    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat df = new SimpleDateFormat(pattern);

    public static final String ARRAY_SUFFIX = "[]";

    public static final String C_STRING = "string";

    public static final String C_ENUM = "enum";

    public static final String C_OBJECT = "object";

    public static final String C_VOID = "void";

    public static final Map<String, String> TYPE_CONVERT = new HashMap<>();

    public static final Map<String, Object> TYPE_VALUE = new HashMap<>();

    static {
        TYPE_CONVERT.put("Boolean", "boolean");
        TYPE_CONVERT.put("Byte", "byte");
        TYPE_CONVERT.put("Short", "short");
        TYPE_CONVERT.put("Integer", "int");
        TYPE_CONVERT.put("Long", "long");
        TYPE_CONVERT.put("Float", "float");
        TYPE_CONVERT.put("Double", "double");
        TYPE_CONVERT.put("String", C_STRING);
        TYPE_CONVERT.put("BigDecimal", "double");
        TYPE_CONVERT.put("Date", "date");
        TYPE_CONVERT.put("Timestamp", "date");
        TYPE_CONVERT.put("LocalDate", "date");
        TYPE_CONVERT.put("LocalTime", "date");
        TYPE_CONVERT.put("LocalDateTime", "date");
        TYPE_VALUE.put("Void", C_VOID);
    }

    static {
        TYPE_VALUE.put("Void", "void");
        TYPE_VALUE.put("Boolean", false);
        TYPE_VALUE.put("Byte", 0);
        TYPE_VALUE.put("Short", 0);
        TYPE_VALUE.put("Integer", 0);
        TYPE_VALUE.put("Long", 0);
        TYPE_VALUE.put("Float", 0.0);
        TYPE_VALUE.put("Double", 0.0);
        TYPE_VALUE.put("String", "");
        TYPE_VALUE.put("BigDecimal", 0.0);
        TYPE_VALUE.put("Date", System.currentTimeMillis());
        TYPE_VALUE.put("Timestamp", System.currentTimeMillis());
        TYPE_VALUE.put("LocalDate", LocalDate.now().toString());
        TYPE_VALUE.put("LocalTime", LocalTime.now().toString());
        TYPE_VALUE.put("LocalDateTime", LocalDateTime.now().toString());

    }

    public static boolean isNormalType(PsiType psiType) {
        String typeName = psiType.getPresentableText();
        return psiType instanceof PsiPrimitiveType || TYPE_VALUE.containsKey(typeName);
    }

    public static boolean isEnum(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        return psiClass != null && psiClass.isEnum();
    }

    public static boolean isVoidType(PsiType psiType) {
        String typeName = psiType.getPresentableText();
        return typeName.equalsIgnoreCase(C_VOID);
    }

    public static boolean isNormalType(String typeName) {
        return TYPE_VALUE.containsKey(typeName);
    }

    public static ConvertFieldType getTypeName(PsiType type) {
        String name;
        Object value;
        TransTypeEnum transTypeEnum;
        if (type == null) {
            return new ConvertFieldType(null, null, TransTypeEnum.VOID);
        }
        String fieldTypeName = type.getPresentableText();
        PsiClass fieldClass = MyPsiSupport.getPsiClass(type);
        if (isVoidType(type)) {
            name = fieldTypeName;
            value = null;
            transTypeEnum = TransTypeEnum.VOID;
        } else if (type instanceof PsiPrimitiveType) {
            name = fieldTypeName;
            value = PsiTypesUtil.getDefaultValue(type);
            transTypeEnum = TransTypeEnum.PRIMITIVE_TYPE;
        } else {
            if (isNormalType(type)) {
                name = TYPE_CONVERT.get(fieldTypeName);
                value = TYPE_VALUE.get(fieldTypeName);
                transTypeEnum = TransTypeEnum.PRIMITIVE_TYPE;
                //Array List
            } else if (type instanceof PsiArrayType || TypeTranslator.collectionTypeList.contains(fieldClass.getQualifiedName())) {
                PsiType deepType = type instanceof PsiArrayType ? type.getDeepComponentType() : PsiUtil.extractIterableTypeParameter(type, false);
                transTypeEnum = type instanceof PsiArrayType ? TransTypeEnum.ARRAY : TransTypeEnum.LIST;
                if (deepType == null) {
                    name = C_OBJECT;
                    value = new Object[0];
                } else {
                    String deepTypeName = deepType.getPresentableText();
                    if (isEnum(deepType)) {
                        name = C_STRING;
                        value = new Object[0];
                    } else if (deepType instanceof PsiPrimitiveType) {
                        name = deepType.getPresentableText();
                        value = new Object[]{PsiTypesUtil.getDefaultValue(deepType)};
                    } else if (isNormalType(deepTypeName)) {
                        name = TYPE_CONVERT.get(deepTypeName);
                        value = new Object[]{TYPE_VALUE.get(deepTypeName)};
                    } else {
                        name = C_OBJECT;
                        value = new Object[0];
                    }
                }
                name = name + ARRAY_SUFFIX;
            } else if (isEnum(type)) {
                transTypeEnum = TransTypeEnum.ENUM;
                name = C_STRING;
                value = C_ENUM;
                //class type
            } else {
                transTypeEnum = TransTypeEnum.OTHER_OBJECT;
                name = C_OBJECT;
                value = C_OBJECT;
            }
        }
        return new ConvertFieldType(name, value, transTypeEnum);
    }

}
