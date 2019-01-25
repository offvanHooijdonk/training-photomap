package by.off.photomap.storage.parse.di

import by.off.photomap.storage.parse.CategoryService
import by.off.photomap.storage.parse.UserService

interface StorageApi {
    fun categoryService(): CategoryService
    fun userService(): UserService
}