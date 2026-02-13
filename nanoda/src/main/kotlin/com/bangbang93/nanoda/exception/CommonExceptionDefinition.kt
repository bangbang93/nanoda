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
}
