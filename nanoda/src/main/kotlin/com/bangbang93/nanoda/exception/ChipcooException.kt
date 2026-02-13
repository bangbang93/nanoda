package com.bangbang93.nanoda.exception

@Suppress("MagicNumber")
open class ChipcooException(
    message: String,
    val code: String,
    val httpCode: Int,
    val data: Map<Any, Any>? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
  companion object {
    @JvmOverloads
    fun from(
        definition: ExceptionDefinition,
        data: Map<Any, Any>? = null,
        cause: Throwable? = null,
    ): ChipcooException =
        ChipcooException(
            message = definition.message,
            code = definition.code,
            httpCode = definition.httpCode,
            data = data,
            cause = cause,
        )
  }

  val isChipcooError = true

  /** 未知错误 */
  class UnknownChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_UNKNOWN", 500, data)

  /** 需要登录 */
  class NeedLoginChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_NEED_LOGIN", 401, data)

  /** 组织错误 */
  class WrongOrganizationChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_WRONG_ORGANIZATION", 400, data)

  /** 找不到对象 */
  class NotFoundChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_NOT_FOUND", 404, data)

  /** 权限不足 */
  class PermissionDeniedChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_PERMISSION_DENIED", 403, data)

  /** 参数缺失 */
  class MissingArgumentChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_MISSING_ARGUMENT", 400, data)

  /** 参数无效 */
  class InvalidArgumentChipcooException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ChipcooException(message, "COMMON_INVALID_ARGUMENT", 400, data)
}
