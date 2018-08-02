#include <jni.h>
#include <string>
#include <cstdlib>
#include "node.h"

extern "C" JNIEXPORT jstring
JNICALL
Java_ml_a0x0000000000_mjavascript_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint
JNICALL
Java_ml_a0x0000000000_mjavascript_MainActivity_startNodeWithArguments(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray arguments) {

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

    // Start node, with argc and argv.
    int nodeResult = node::Start(argc, argv);
    free(argBuff);

    return jint(nodeResult);
}