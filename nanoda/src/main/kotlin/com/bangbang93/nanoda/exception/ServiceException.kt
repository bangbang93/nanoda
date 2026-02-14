package com.bangbang93.nanoda.exception

@Suppress("MagicNumber")
open class ServiceException(
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
    ): ServiceException =
        ServiceException(
            message = definition.message,
            code = definition.code,
            httpCode = definition.httpCode,
            data = data,
            cause = cause,
        )
  }

  @Suppress("VariableNaming") val `$isServiceError` = true

  /** 未知错误 */
  class UnknownServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_UNKNOWN", 500, data)

  /** 需要登录 */
  class NeedLoginServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_NEED_LOGIN", 401, data)

  /** 组织错误 */
  class WrongOrganizationServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_WRONG_ORGANIZATION", 400, data)

  /** 找不到对象 */
  class NotFoundServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_NOT_FOUND", 404, data)

  /** 权限不足 */
  class PermissionDeniedServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_PERMISSION_DENIED", 403, data)

  /** 参数缺失 */
  class MissingArgumentServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_MISSING_ARGUMENT", 400, data)

  /** 参数无效 */
  class InvalidArgumentServiceException(
      message: String,
      cause: Throwable? = null,
      data: Map<Any, Any>? = null,
  ) : ServiceException(message, "COMMON_INVALID_ARGUMENT", 400, data)
}
