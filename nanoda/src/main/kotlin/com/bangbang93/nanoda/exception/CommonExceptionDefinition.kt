package com.bangbang93.nanoda.exception

@Suppress("MagicNumber")
object CommonExceptionDefinition {
  object Unknown : ExceptionDefinition {
    override val message: String = "未知错误"
    override val code: String = "COMMON_UNKNOWN"
    override val httpCode: Int = 500
  }

  object NeedLogin : ExceptionDefinition {
    override val message: String = "需要登录"
    override val code: String = "COMMON_NEED_LOGIN"
    override val httpCode: Int = 401
  }

  object PermissionDenied : ExceptionDefinition {
    override val message: String = "访问拒绝"
    override val code: String = "COMMON_PERMISSION_DENIED"
    override val httpCode: Int = 403
  }

  object NoSuchObject : ExceptionDefinition {
    override val message: String = "找不到对象"
    override val code: String = "COMMON_NO_SUCH_OBJECT"
    override val httpCode: Int = 404
  }

  object ThirdParty : ExceptionDefinition {
    override val message: String = "第三方服务错误"
    override val code: String = "COMMON_THIRD_PARTY_ERROR"
    override val httpCode: Int = 502
  }

  object InvalidArgument : ExceptionDefinition {
    override val message: String = "无效参数"
    override val code: String = "COMMON_INVALID_ARGUMENT"
    override val httpCode: Int = 400
  }

  object MissingArgument : ExceptionDefinition {
    override val message: String = "缺失参数"
    override val code: String = "COMMON_MISSING_ARGUMENT"
    override val httpCode: Int = 400
  }

  object AlreadyExists : ExceptionDefinition {
    override val message: String = "对象已存在"
    override val code: String = "COMMON_ALREADY_EXISTS"
    override val httpCode: Int = 409
  }

  object WrongStatus : ExceptionDefinition {
    override val message: String = "状态错误"
    override val code: String = "COMMON_WRONG_STATUS"
    override val httpCode: Int = 406
  }
}
