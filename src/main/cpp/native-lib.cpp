#include <jni.h>
#include <string>
#include <cstdlib>
#include "node.h"
#include <pthread.h>
#include <unistd.h>
#include <android/log.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>

extern "C" JNIEXPORT jstring
JNICALL
Java_ml_a0x0000000000_mjavascript_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

const char *ADB_TAG = "NODEJS";

int pipe_stdout[2];
int pipe_stderr[2];
pthread_t thread_stdout;
pthread_t thread_stderr;
JavaVM *vm = NULL;
jobject jObj = NULL;

void *thread_stderr_func(void*) {
    JNIEnv *env = NULL;
    if(vm->AttachCurrentThread(&env, NULL) != JNI_OK || env == NULL) {
        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, "Cannot get env");
    } else {
        __android_log_write(ANDROID_LOG_INFO, ADB_TAG, "Env got");
    };
    jclass cls = env->GetObjectClass(jObj);
    jmethodID onResult = env->GetMethodID(cls, "onResult", "(Ljava/lang/String;)V");
    ssize_t redirectSize;
    char buf[2048];
    while((redirectSize = read(pipe_stderr[0], buf, sizeof buf - 1)) > 0) {
        // __android_log will add a new line anyway.
//        if(buf[redirectSize - 1] == '\n')
//            --redirectSize;
        buf[redirectSize] = 0;
//        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, buf);
        env->CallVoidMethod(jObj, onResult, env->NewStringUTF(buf));
    }
    return 0;
}

void *thread_stdout_func(void*) {
    JNIEnv *env = NULL;
    if(vm->AttachCurrentThread(&env, NULL) != JNI_OK || env == NULL) {
        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, "Cannot get env");
    } else {
        __android_log_write(ANDROID_LOG_INFO, ADB_TAG, "Env got");
    };
    jclass cls = env->GetObjectClass(jObj);
    jmethodID onResult = env->GetMethodID(cls, "onResult", "(Ljava/lang/String;)V");
    ssize_t redirectSize;
    char buf[2048];
    while((redirectSize = read(pipe_stdout[0], buf, sizeof buf - 1)) > 0) {
//        // __android_log will add a new line anyway.
//        if(buf[redirectSize - 1] == '\n')
//            --redirectSize;
        buf[redirectSize] = 0;
//        __android_log_write(ANDROID_LOG_INFO, ADB_TAG, buf);
        env->CallVoidMethod(jObj, onResult, env->NewStringUTF(buf));
    }
    return 0;
}

int startRedirectingStdoutStderr() {
    // set stdout as unbuffered.
    setvbuf(stdout, 0, _IONBF, 0);
    pipe(pipe_stdout);
    dup2(pipe_stdout[1], STDOUT_FILENO);

    // set stderr as unbuffered.
    setvbuf(stderr, 0, _IONBF, 0);
    pipe(pipe_stderr);
    dup2(pipe_stderr[1], STDERR_FILENO);

    if(pthread_create(&thread_stdout, 0, thread_stdout_func, 0) == -1)
        return -1;
    pthread_detach(thread_stdout);

    if(pthread_create(&thread_stderr, 0, thread_stderr_func, 0) == -1)
        return -1;
    pthread_detach(thread_stderr);

    return 0;
}

/*
void redirectSTDIN(std::string infile) {

//    int outFD = -1;
//    if(mkfifo(outfile.c_str(), 0644) && errno != EEXIST) {
//        __android_log_print(ANDROID_LOG_ERROR, ADB_TAG, "Cannot make FIFO file for stdout");
//        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, strerror(errno));
//    } else outFD = open(outfile.c_str(), O_RDWR);
//    __android_log_write(ANDROID_LOG_DEBUG, ADB_TAG, std::to_string(outFD).c_str());
//
//    int errFD = -1;
//    if(outfile == errfile) {
//        errFD = outFD;
//    } else {
//        if(mkfifo(errfile.c_str(), 0644) && errno != EEXIST) {
//            __android_log_print(ANDROID_LOG_ERROR, ADB_TAG, "Cannot make FIFO file for stderr");
//            __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, strerror(errno));
//        } else errFD = open(errfile.c_str(), O_RDWR);
//    }
//    __android_log_write(ANDROID_LOG_DEBUG, ADB_TAG, std::to_string(errFD).c_str());
//    sem_post(&sem);

    int inFD = -1;
    if(mkfifo(infile.c_str(), 0644) && errno != EEXIST) {
        __android_log_print(ANDROID_LOG_ERROR, ADB_TAG, "Cannot make FIFO file for stdin");
        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, strerror(errno));
    } else inFD = open(infile.c_str(), O_RDWR);
    __android_log_write(ANDROID_LOG_DEBUG, ADB_TAG, std::to_string(inFD).c_str());

//    dup2(outFD, STDOUT_FILENO);
//    setvbuf(stdout, 0, _IONBF, 0);
//
//    dup2(errFD, STDERR_FILENO);
//    setvbuf(stderr, 0, _IONBF, 0);
//    close(outFD);
//    if(errFD != outFD) close(errFD);

    dup2(inFD, STDIN_FILENO);
    close(inFD);
    __android_log_print(ANDROID_LOG_INFO, ADB_TAG, "stdin redirected");
}
*/

extern "C" JNIEXPORT jint
JNICALL
Java_ml_a0x0000000000_mjavascript_NodeJavaScript_startNodeWithArguments(
        JNIEnv *env,
        jobject /* this */ obj,
        jobjectArray arguments) {

    jObj = env->NewGlobalRef(obj);

    if(env->GetJavaVM(&vm) != JNI_OK) {
        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, "Couldn't get JavaVM.");
    } else {
        __android_log_write(ANDROID_LOG_INFO, ADB_TAG, "JavaVM got");
    }

    // argc
    jsize argc = env->GetArrayLength(arguments);

    // Compute byte size need for all arguments in contiguous memory.
    size_t argSize = 0;
    for (int i = 0; i < argc; i++) {
        argSize += strlen(env->GetStringUTFChars((jstring)env->GetObjectArrayElement(arguments, i), 0));
        argSize++; // for '\0'
    }

    // Stores arguments in contiguous memory.
    char* argBuff = (char*) calloc(argSize, sizeof(char));

    // argv to pass into node.
    char* argv[argc];

    // To iterate through the expected start position of each argument in args_buffer.
    char* currentArgsPosition = argBuff;

    // Populate the args_buffer and argv.
    for (int i = 0; i < argc; i++) {
        const char* currentArgument = env->GetStringUTFChars((jstring)env->GetObjectArrayElement(arguments, i), 0);

        // Copy current argument to its expected position in args_buffer
        strncpy(currentArgsPosition, currentArgument, strlen(currentArgument));

        // Save current argument start position in argv
        argv[i] = currentArgsPosition;

        // Increment to the next argument's expected position.
        currentArgsPosition += strlen(currentArgsPosition) + 1;
    }

    // Redirect stdout, stderr
    if (startRedirectingStdoutStderr() == -1) {
        __android_log_write(ANDROID_LOG_ERROR, ADB_TAG, "Couldn't start redirecting stdout and stderr.");
    }

    // Start node, with argc and argv.
    __android_log_print(ANDROID_LOG_INFO, ADB_TAG, "Starting Node.js");
    int nodeResult = node::Start(argc, argv);
    free(argBuff);
    __android_log_print(ANDROID_LOG_INFO, ADB_TAG, "Node.js exited with code %d", nodeResult);

    return jint(nodeResult);
}