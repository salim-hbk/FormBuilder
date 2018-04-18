package com.salt.formbuilder

import com.itcebook.util.FormBuilder

/**
 * Created by Salim.UM on 20-03-2018.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class FormElement(val formType: Int = FormBuilder.TEXT_VIEW,
                             val fieldOrder: Int = 0, val fieldBGresource: Int = 0,
                             val fieldPadding: Int = 0, val fieldPaddingLeft: Int = 0,
                             val fieldPaddingRight: Int = 0, val fieldPaddingTop: Int = 0,
                             val fieldPaddingBottom: Int = 0, val fieldMargin: Int = 0,
                             val fieldMarginLeft: Int = 0, val fieldMarginRight: Int = 0,
                             val fieldMarginTop: Int = 0, val fieldMarginBottom: Int = 0,
                             val fieldTextHight: Float = 14f, val fieldHight: Int = 0,
                             val fieldTextColor: Int = 0, val fieldHint: Int = 0, val fieldText: Int = 0,
                             val fieldError: Int = 0)