package org.zb.plugin.restdoc.constant;

import com.google.common.collect.Lists;

import java.util.List;

public class CommonConstant {

    public static final String CONSTRAINTS_NOTNULL = "javax.validation.constraints.NotNull";

    public static final String CONSTRAINTS_NOTBLANK = "javax.validation.constraints.NotBlank";

    public static final String CONSTRAINTS_NOTBLANK2 = "org.hibernate.validator.constraints.NotBlank";

    public static final String CONSTRAINTS_NOTMPTY = "javax.validation.constraints.NotEmpty";

    public static final List<String> IGNORE_MODEL = Lists.newArrayList("javax.servlet.http.HttpServletRequest",
            "javax.servlet.http.HttpServletResponse","org.springframework.web.servlet.ModelAndView");


    public static final String SWAGGER_DOC = "io.swagger.annotations.ApiModelProperty";

    public static final String SWAGGER_API =  "io.swagger.annotations.ApiOperation";







}
