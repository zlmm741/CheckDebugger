#include <jni.h>
#include <string>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_droider_checkdebugger_MainActivity_checkStatus(
        JNIEnv* env,
        jobject /* this */) {
    FILE* f = fopen("/proc/self/status", "r");
    char buf[1024];
    while (fgets(buf, 1024, f)) {
        if (strstr(buf, "TracerPid:")) {
            int tpid;
            sscanf(buf, "TracerPid: %d", &tpid);
            if (tpid != 0) {
                fclose(f);
                return true;
            }
        }
    }
    fclose(f);
    return false;
}
