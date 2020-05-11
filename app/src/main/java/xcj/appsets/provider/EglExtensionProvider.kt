package xcj.appsets.provider

import android.opengl.GLES10
import android.text.TextUtils
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

object EglExtensionProvider {
    val eglExtensions: List<String>
        get() {
            val glExtensions: MutableSet<String> =
                HashSet()
            val egl10 = EGLContext.getEGL() as EGL10 ?: return ArrayList()
            val display = egl10.eglGetDisplay(
                EGL10.EGL_DEFAULT_DISPLAY
            )
            egl10.eglInitialize(display, IntArray(2))
            val cf = IntArray(1)
            if (egl10.eglGetConfigs(display, null, 0, cf)) {
                val configs =
                    arrayOfNulls<EGLConfig>(cf[0])
                if (egl10.eglGetConfigs(display, configs, cf[0], cf)) {
                    val a1 = intArrayOf(
                        EGL10.EGL_WIDTH,
                        EGL10.EGL_PBUFFER_BIT,
                        EGL10.EGL_HEIGHT,
                        EGL10.EGL_PBUFFER_BIT,
                        EGL10.EGL_NONE
                    )
                    val a2 = intArrayOf(12440, EGL10.EGL_PIXMAP_BIT, EGL10.EGL_NONE)
                    val a3 = IntArray(1)
                    for (i in 0 until cf[0]) {
                        egl10.eglGetConfigAttrib(display, configs[i], EGL10.EGL_CONFIG_CAVEAT, a3)
                        if (a3[0] != EGL10.EGL_SLOW_CONFIG) {
                            egl10.eglGetConfigAttrib(
                                display,
                                configs[i],
                                EGL10.EGL_SURFACE_TYPE,
                                a3
                            )
                            if (1 and a3[0] != 0) {
                                egl10.eglGetConfigAttrib(
                                    display,
                                    configs[i],
                                    EGL10.EGL_RENDERABLE_TYPE,
                                    a3
                                )
                                if (1 and a3[0] != 0) {
                                    addExtensionsForConfig(
                                        egl10,
                                        display,
                                        configs[i],
                                        a1,
                                        null,
                                        glExtensions
                                    )
                                }
                                if (4 and a3[0] != 0) {
                                    addExtensionsForConfig(
                                        egl10,
                                        display,
                                        configs[i],
                                        a1,
                                        a2,
                                        glExtensions
                                    )
                                }
                            }
                        }
                    }
                }
            }
            egl10.eglTerminate(display)
            val sorted: List<String> =
                ArrayList(glExtensions)
            Collections.sort(sorted)
            return sorted
        }

    private fun addExtensionsForConfig(
        egl10: EGL10,
        egldisplay: EGLDisplay,
        eglconfig: EGLConfig?,
        ai: IntArray,
        ai1: IntArray?,
        set: MutableSet<String>
    ) {
        val eglContext =
            egl10.eglCreateContext(egldisplay, eglconfig, EGL10.EGL_NO_CONTEXT, ai1)
        if (eglContext === EGL10.EGL_NO_CONTEXT) {
            return
        }
        val eglSurface =
            egl10.eglCreatePbufferSurface(egldisplay, eglconfig, ai)
        if (eglSurface === EGL10.EGL_NO_SURFACE) {
            egl10.eglDestroyContext(egldisplay, eglContext)
        } else {
            egl10.eglMakeCurrent(egldisplay, eglSurface, eglSurface, eglContext)
            val s = GLES10.glGetString(7939)
            if (!TextUtils.isEmpty(s)) {
                val `as` = s.split(" ").toTypedArray()
                val i = `as`.size
                set.addAll(Arrays.asList(*`as`).subList(0, i))
            }
            egl10.eglMakeCurrent(
                egldisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
            egl10.eglDestroySurface(egldisplay, eglSurface)
            egl10.eglDestroyContext(egldisplay, eglContext)
        }
    }
}