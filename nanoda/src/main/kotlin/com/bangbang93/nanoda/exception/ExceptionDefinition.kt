package com.bangbang93.nanoda.exception

interface ExceptionDefinition {
  val message: String
  val code: String
  val httpCode: Int
}
