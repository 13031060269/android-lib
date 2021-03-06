package com.lwp.lib.host.utils

import android.content.ComponentName
import android.util.Log
import com.lwp.lib.host.DEBUG
import com.lwp.lib.host.BUFF_SIZE
import com.lwp.lib.host.KEY_SPLIT
import com.lwp.lib.host.TAG
import java.io.*
import java.lang.reflect.Field

fun printLog(info: String) {
    if (DEBUG) {
        Log.e(TAG, info)
    }
}

fun parseComponent(component: String?): ComponentName? {
    val split = component?.split(KEY_SPLIT)
    if (split?.size == 2) {
        return ComponentName(split[0], split[1])
    }
    return null
}

fun formatComponent(component: ComponentName): String {
    return "${component.packageName}$KEY_SPLIT${component.className}"
}


internal object HostUtils {
    @JvmStatic
    fun saveToFile(dataIns: InputStream, target: File) {
        dataIns.use {
            BufferedOutputStream(
                FileOutputStream(target)
            ).use {
                var count: Int
                val data = ByteArray(BUFF_SIZE)
                while (dataIns.read(data, 0, BUFF_SIZE).apply { count = this } != -1) {
                    it.write(data, 0, count)
                }
            }
        }
    }

    @JvmStatic
    fun saveToFile(data: ByteArray, target: File) {
        saveToFile(ByteArrayInputStream(data), target)
    }

    @JvmStatic
    fun saveToFile(source: File, target: File) {
        saveToFile(FileInputStream(source), target)
    }

    @JvmStatic
    @Throws(
        IllegalAccessException::class,
        IllegalArgumentException::class,
        NoSuchFieldException::class
    )
    fun <T> getFieldValue(
        obj: Any?,
        fieldName: String,
        resolveParent: Boolean
    ): T {
        val rs = getField(obj, fieldName, resolveParent)
            ?: throw NoSuchFieldException("field:$fieldName")
        val field = rs[0] as Field?
        val targetObj = rs[1]
        return field!![targetObj] as T
    }

    @JvmStatic
    @Throws(
        IllegalAccessException::class,
        IllegalArgumentException::class,
        NoSuchFieldException::class
    )
    fun setFieldValue(
        obj: Any?,
        fieldName: String,
        `val`: Any?,
        resolveParent: Boolean
    ) {
        val rs = getField(obj, fieldName, resolveParent)
            ?: throw NoSuchFieldException("field:$fieldName")
        val field = rs[0] as Field?
        val targetObj = rs[1]
        field!![targetObj] = `val`
    }

    @Throws(
        IllegalAccessException::class,
        IllegalArgumentException::class,
        NoSuchFieldException::class
    )
    private fun getField(
        obj: Any?,
        elFieldName: String,
        resolveParent: Boolean
    ): Array<Any?>? {
        if (obj == null) {
            return null
        }
        val fieldNames = elFieldName.split("[.]".toRegex()).toTypedArray()
        var targetObj: Any = obj
        var targetClass: Class<*> = targetObj.javaClass
        var `val`: Any?
        var i = 0
        var field: Field?
        val rs = arrayOfNulls<Any>(2)
        for (fName in fieldNames) {
            i++
            field = field(targetClass, fName, resolveParent)
            field!!.isAccessible = true
            rs[0] = field
            rs[1] = targetObj
            `val` = field[targetObj]
            if (`val` == null) {
                if (i < fieldNames.size) {
                    throw IllegalAccessException(
                        "can not getFieldValue as field '" + fName
                                + "' value is null in '"
                                + targetClass.name + "'"
                    )
                }
                break
            }
            targetObj = `val`
            targetClass = targetObj.javaClass
        }
        return rs
    }

    @Throws(
        IllegalAccessException::class,
        IllegalArgumentException::class,
        NoSuchFieldException::class
    )
    private fun field(
        targetClass: Class<*>?,
        fieldName: String?,
        resolveParent: Boolean
    ): Field? {
        var targetClass = targetClass
        var noSuchFieldExceptionOccor: NoSuchFieldException? = null
        var rsField: Field? = null
        try {
            val field = targetClass!!.getDeclaredField(fieldName!!)
            rsField = field
            if (!resolveParent) {
                field.isAccessible = true
                return field
            }
        } catch (e: NoSuchFieldException) {
            noSuchFieldExceptionOccor = e
        }
        if (noSuchFieldExceptionOccor != null) {
            if (resolveParent) {
                while (true) {
                    targetClass = targetClass!!.superclass
                    if (targetClass == null) {
                        break
                    }
                    try {
                        val field = targetClass.getDeclaredField(fieldName!!)
                        field.isAccessible = true
                        return field.also { rsField = it }
                    } catch (e: NoSuchFieldException) {
                        if (targetClass.superclass == null) {
                            throw e
                        }
                    }
                }
            } else {
                throw noSuchFieldExceptionOccor
            }
        }
        return rsField
    }

//    @JvmStatic
//    fun fileMd5(file: File): String {
//        return FileInputStream(file).use {
//            val md5: MessageDigest = MessageDigest.getInstance("MD5")
//            val buffer = ByteArray(8192)
//            var length: Int
//            while (it.read(buffer).apply { length = this } != -1) {
//                md5.update(buffer, 0, length)
//            }
//            BigInteger(1, md5.digest()).toString(16)
//        }
//    }
}
