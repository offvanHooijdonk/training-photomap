package by.off.photomap.storage.parse.di

import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.UserService

interface StorageApi {
    fun userService(): UserService
    fun photoService(): PhotoService
    fun geoPointService(): GeoPointService
}