package com.lyd.absolverdatabase

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class MyGlideModel:AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
//        val calculator: MemorySizeCalculator = MemorySizeCalculator.Builder(context.applicationContext).build()
//        val defaultMemoryCacheSize: Int = calculator.memoryCacheSize
//        val defaultBitmapPoolSize: Int = calculator.bitmapPoolSize
//        val defaultArrayPoolSize: Int = calculator.arrayPoolSizeInBytes
//        builder.setDefaultRequestOptions(
//            RequestOptions()
//                .format(DecodeFormat.PREFER_RGB_565)
//        )
//        builder.setMemoryCache(LruResourceCache((defaultMemoryCacheSize / 2).toLong()))
//        builder.setBitmapPool(LruBitmapPool((defaultBitmapPoolSize / 2).toLong()))
//        builder.setArrayPool(LruArrayPool(defaultArrayPoolSize / 2))
    }
}