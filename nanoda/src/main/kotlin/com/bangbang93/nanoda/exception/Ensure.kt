@file:OptIn(ExperimentalContracts::class)

package com.bangbang93.nanoda.exception

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** 确保值不为 null，否则抛出 [ServiceException.MissingArgumentServiceException] */
inline fun ensureNotNull(
    value: Any?,
    lazyMessage: () -> String,
) {
  contract { returns() implies (value != null) }
  if (value == null) {
    throw ServiceException.MissingArgumentServiceException(lazyMessage())
  }
}

/** 确保值为 true，否则抛出 [ServiceException.InvalidArgumentServiceException] */
inline fun ensure(
    value: Boolean,
    lazyMessage: () -> String,
) {
  contract { returns() implies value }
  if (!value) {
    throw ServiceException.InvalidArgumentServiceException(lazyMessage())
  }
}
