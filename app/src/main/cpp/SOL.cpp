#include <jni.h>
#include <cstring>
#include <cctype>
extern "C"
JNIEXPORT void JNICALL
Java_no_SOL_MainActivity_replaceVowelsWithK(
        JNIEnv* env,
        jobject,
        jobject buffer,
        jint length) {

    char* data = static_cast<char*>(env->GetDirectBufferAddress(buffer));
    if (data == nullptr) return;

    for (int i = 0; i < length; ++i) {
        char c = data[i];
        char lower = std::tolower(c);
        if (lower == 'a' || lower == 'e' || lower == 'i' || lower == 'o' || lower == 'u') {
            data[i] = 'k';
        }
    }
}


// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("SOL");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("SOL")
//      }
//    }