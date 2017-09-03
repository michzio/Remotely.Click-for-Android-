#include <jni.h>
#include <stdio.h>
#include <networking/stream_client.h>
#include "client.h"
#include "networking/client_info.h"
#include "config.h"

static JavaVM *javaVM;
static jobject globalObjClientInfo = 0;

// function prototypes
client_info_t *getClientInfoCStruct(JNIEnv* env, jobject objClientInfo);

// Client connection callbacks
void on_connection_start(const sock_fd_t sock_fd, void *callback_arg);
void on_connection_authenticated(const sock_fd_t sock_fd, void *callback_arg);
void on_connection_end(const sock_fd_t sock_fd, void *callback_arg);
void on_connection_error(const sock_fd_t sock_fd, const int error_code, const char *error_msg, void *callback_arg);

JNIEXPORT void JNICALL
Java_click_remotely_networking_RemoteControllerClient_connect(
        JNIEnv* env,
        jobject objCalling,
        jobject objClientInfo) {


    // it is generally unsafe to cache a JNIEnv * instance as it varies
    // depending on the currently active thread. So it is better to save
    // a JavaWM * instance, which will never change.
    int status = (*env)->GetJavaVM(env, &javaVM);
    if(status != 0) {
        // Failed!
        return;
    }

    // convert local to global reference
    // if(globalObjClientInfo != NULL) (*env)->DeleteGlobalRef(env, globalObjClientInfo);
    globalObjClientInfo = (*env)->NewGlobalRef(env, objClientInfo);
    // convert java object ClientInfo into C struct client_into_t
    client_info_t *client_info = getClientInfoCStruct(env, globalObjClientInfo);

    // set client info callbacks
    client_info_set_connection_start_callback(client_info, on_connection_start);
    client_info_set_connection_authenticated_callback(client_info, on_connection_authenticated);
    client_info_set_connection_end_callback(client_info, on_connection_end);
    client_info_set_connection_error_callback(client_info, on_connection_error);

    start_client(rpc_stream_client, client_info);

    return;
}

client_info_t *getClientInfoCStruct(JNIEnv* env, jobject objClientInfo) {

    const char *pasvPort = NULL;
    const char *pasvIp = NULL;
    const char *connPort = NULL;
    const char *connIp = NULL;
    int sockFd = 0;
    const char *securityPassword = NULL;
    const char *clientIdentity = NULL;
    const char *clientOS = NULL;

    jclass classClientInfo = (*env)->GetObjectClass(env, objClientInfo);

    // read pasvPort field
    jmethodID midGetPasvPort = (*env)->GetMethodID(env, classClientInfo, "getPasvPort", "()Ljava/lang/String;");
    if(midGetPasvPort == NULL) {
        return NULL;
    }
    jstring strPasvPort = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetPasvPort);
    if(strPasvPort != NULL) {
        pasvPort = (*env)->GetStringUTFChars(env, strPasvPort, 0);
    }

    // read pasvIp field
    jmethodID midGetPasvIp = (*env)->GetMethodID(env, classClientInfo, "getPasvIp", "()Ljava/lang/String;");
    if(midGetPasvIp == NULL) {
        return NULL;
    }
    jstring strPasvIp = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetPasvIp);
    if(strPasvIp != NULL) {
        pasvIp = (*env)->GetStringUTFChars(env, strPasvIp, 0);
    }

    // read connPort field
    jmethodID midGetConnPort = (*env)->GetMethodID(env, classClientInfo, "getConnPort", "()Ljava/lang/String;");
    if(midGetConnPort == NULL) {
        return NULL;
    }
    jstring strConnPort = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetConnPort);
    if(strConnPort != NULL) {
        connPort = (*env)->GetStringUTFChars(env, strConnPort, 0);
    }

    // read connIp field
    jmethodID midGetConnIp = (*env)->GetMethodID(env, classClientInfo, "getConnIp", "()Ljava/lang/String;");
    if(midGetConnIp == NULL) {
        return NULL;
    }
    jstring strConnIp = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetConnIp);
    if(strConnIp != NULL) {
        connIp = (*env)->GetStringUTFChars(env, strConnIp, 0);
    }

    // read sockFd field
    jmethodID midGetSockFd = (*env)->GetMethodID(env, classClientInfo, "getSockFd", "()I");
    if(midGetSockFd == NULL) {
        return NULL;
    }
    jint intSockFd = (*env)->CallIntMethod(env, objClientInfo, midGetSockFd);
    sockFd = (int) intSockFd;

    // read securityPassword field
    jmethodID midGetSecurityPassword = (*env)->GetMethodID(env, classClientInfo, "getSecurityPassword", "()Ljava/lang/String;");
    if(midGetSecurityPassword == NULL) {
        return NULL;
    }
    jstring strSecurityPassword = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetSecurityPassword);
    if(strSecurityPassword != NULL) {
        securityPassword = (*env)->GetStringUTFChars(env, strSecurityPassword, 0);
    }

    // read clientIdentity field
    jmethodID midGetClientIdentity = (*env)->GetMethodID(env, classClientInfo, "getClientIdentity", "()Ljava/lang/String;");
    if(midGetClientIdentity == NULL) {
        return NULL;
    }
    jstring strClientIdentity = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetClientIdentity);
    if(strClientIdentity != NULL) {
        clientIdentity = (*env)->GetStringUTFChars(env, strClientIdentity, 0);
    }

    // read clientOS field
    jmethodID midGetClientOS = (*env)->GetMethodID(env, classClientInfo, "getClientOS", "()Ljava/lang/String;");
    if(midGetClientOS == NULL) {
        return NULL;
    }
    jstring strClientOS = (jstring) (*env)->CallObjectMethod(env, objClientInfo, midGetClientOS);
    if(strClientOS != NULL) {
        clientOS = (*env)->GetStringUTFChars(env, strClientOS, 0);
    }

    // create client info object to establish connection
    client_info_t *client_info;
    client_info_init(&client_info);

    // set basic client info
    if(pasvPort != NULL) client_info_set_pasv_port(client_info, pasvPort);
    if(pasvIp != NULL) client_info_set_pasv_ip(classClientInfo, pasvIp);
    if(connPort != NULL) client_info_set_conn_port(client_info, connPort);
    if(connIp != NULL) client_info_set_conn_ip(client_info, connIp);
    client_info_set_sock(client_info, sockFd);
    client_info_set_security_password(client_info, securityPassword);
    client_info_set_client_identity(client_info, clientIdentity);
    client_info_set_client_os(client_info, clientOS);

    return client_info;
}

JNIEXPORT void JNICALL
Java_click_remotely_networking_RemoteControllerClient_disconnect(
        JNIEnv* env,
        jobject objCalling,
        jobject objClientInfo ) {

    // convert java object ClientInfo into C struct client_into_t
    client_info_t *client_info = getClientInfoCStruct(env, objClientInfo);

    // set client info callbacks
    client_info_set_connection_start_callback(client_info, on_connection_start);
    client_info_set_connection_authenticated_callback(client_info, on_connection_authenticated);
    client_info_set_connection_end_callback(client_info, on_connection_end);
    client_info_set_connection_error_callback(client_info, on_connection_error);

    end_client(client_info);
}


void on_connection_start(const sock_fd_t sock_fd, void *callback_arg) {

    printf("Client connection on socket: %d started.\n", sock_fd);

    JNIEnv *env;
    (*javaVM)->AttachCurrentThread(javaVM, (void **)&env, NULL);

    jclass classClientInfo = (*env)->GetObjectClass(env, globalObjClientInfo);
    jmethodID midGetConnectionStartListener = (*env)->GetMethodID(env, classClientInfo, "getConnectionStartListener", "()Lclick/remotely/networking/ClientInfo$OnConnectionStartListener;" );
    if(midGetConnectionStartListener == NULL) {
        return;
    }
    jobject objConnectionStartListener = (*env)->CallObjectMethod(env, globalObjClientInfo, midGetConnectionStartListener);
    if(objConnectionStartListener == NULL) {
        return;
    }
    jclass interfaceOnConnectionStartListener = (*env)->GetObjectClass(env, objConnectionStartListener);
    jmethodID midOnConnectionStart = (*env)->GetMethodID(env, interfaceOnConnectionStartListener,
                                        "onConnectionStart", "(ILjava/lang/Object;)V");
    if(midOnConnectionStart == NULL) {
        return;
    }

    (*env)->CallVoidMethod(env, objConnectionStartListener, midOnConnectionStart, sock_fd, callback_arg);
}

void on_connection_authenticated(const sock_fd_t sock_fd, void *callback_arg) {

    printf("Client connection on socket: %d authenticated.\n", sock_fd);

    JNIEnv *env;
    (*javaVM)->AttachCurrentThread(javaVM, (void **)&env, NULL);

    jclass classClientInfo = (*env)->GetObjectClass(env, globalObjClientInfo);
    jmethodID midGetConnectionAuthenticatedListener = (*env)->GetMethodID(env, classClientInfo, "getConnectionAuthenticatedListener", "()Lclick/remotely/networking/ClientInfo$OnConnectionAuthenticatedListener;");
    if(midGetConnectionAuthenticatedListener == NULL) {
        return;
    }
    jobject objConnectionAuthenticatedListener = (*env)->CallObjectMethod(env, globalObjClientInfo, midGetConnectionAuthenticatedListener);
    if(objConnectionAuthenticatedListener == NULL) {
        return;
    }
    jclass interfaceOnConnectionAuthenticatedListener = (*env)->GetObjectClass(env, objConnectionAuthenticatedListener);
    jmethodID midOnConnectionAuthenticated = (*env)->GetMethodID(env, interfaceOnConnectionAuthenticatedListener,
                                                "onConnectionAuthenticated", "(ILjava/lang/Object;)V");
    if(midOnConnectionAuthenticated == NULL) {
        return;
    }

    (*env)->CallVoidMethod(env, objConnectionAuthenticatedListener, midOnConnectionAuthenticated, sock_fd, callback_arg);
}

void on_connection_end(const sock_fd_t sock_fd, void *callback_arg) {

    printf("Client connection on socket: %d ended.\n", sock_fd);

    JNIEnv *env;
    (*javaVM)->AttachCurrentThread(javaVM, (void **) &env, NULL);

    jclass classClientInfo = (*env)->GetObjectClass(env, globalObjClientInfo);
    jmethodID midGetConnectionEndListener = (*env)->GetMethodID(env, classClientInfo, "getConnectionEndListener", "()Lclick/remotely/networking/ClientInfo$OnConnectionEndListener;");
    if(midGetConnectionEndListener == NULL) {
        // when client connection ends then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }
    jobject objConnectionEndListener = (*env)->CallObjectMethod(env, globalObjClientInfo, midGetConnectionEndListener);
    if(objConnectionEndListener == NULL) {
        // when client connection ends then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }
    jclass interfaceOnConnectionEndListener = (*env)->GetObjectClass(env, objConnectionEndListener);
    jmethodID midOnConnectionEnd = (*env)->GetMethodID(env, interfaceOnConnectionEndListener,
                                        "onConnectionEnd", "(ILjava/lang/Object;)V");
    if(midOnConnectionEnd == NULL) {
        // when client connection ends then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }

    (*env)->CallVoidMethod(env, objConnectionEndListener, midOnConnectionEnd, sock_fd, callback_arg);

    // when client connection ends then release global ClientInfo object reference.
    //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
}

void on_connection_error(const sock_fd_t sock_fd, const int error_code, const char *error_msg, void *callback_arg) {

    printf("Client connection on socket: %d failed with error: %d, %s.\n", sock_fd, error_code, error_msg);

    JNIEnv *env;
    (*javaVM)->AttachCurrentThread(javaVM, (void **) &env, NULL);

    jclass classClientInfo = (*env)->GetObjectClass(env, globalObjClientInfo);
    jmethodID midGetConnectionErrorListener = (*env)->GetMethodID(env, classClientInfo, "getConnectionErrorListener", "()Lclick/remotely/networking/ClientInfo$OnConnectionErrorListener;");
    if(midGetConnectionErrorListener == NULL) {
        // when client connection end with error then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }
    jobject objConnectionErrorListener = (*env)->CallObjectMethod(env, globalObjClientInfo, midGetConnectionErrorListener);
    if(objConnectionErrorListener == NULL) {
        // when client connection end with error then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }
    jclass interfaceOnConnectionErrorListener = (*env)->GetObjectClass(env, objConnectionErrorListener);
    jmethodID midOnConnectionError = (*env)->GetMethodID(env, interfaceOnConnectionErrorListener,
                                        "onConnectionError", "(IILjava/lang/String;Ljava/lang/Object;)V");
    if(midOnConnectionError == NULL) {
        // when client connection end with error then release global ClientInfo object reference.
        //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
        return;
    }

    (*env)->CallVoidMethod(env, objConnectionErrorListener, midOnConnectionError, sock_fd, error_code, (*env)->NewStringUTF(env, error_msg), callback_arg);

    // when client connection end with error then release global ClientInfo object reference.
    //(*env)->DeleteGlobalRef(env, globalObjClientInfo);
}


JNIEXPORT void JNICALL
Java_click_remotely_networking_RemoteControllerClient_sendMessage(
        JNIEnv* env,
        jobject objCalling,
        jobject objClientInfo,
        jstring strMessage ) {

    jclass classClientInfo = (*env)->GetObjectClass(env, objClientInfo);
    jmethodID midGetSockFd = (*env)->GetMethodID(env, classClientInfo, "getSockFd", "()I");
    if(midGetSockFd == NULL) {
        return;
    }
    int intSockFd = (int) (*env)->CallIntMethod(env, objClientInfo, midGetSockFd);
    if(intSockFd < 0) {
        return;
    }

    const char *message = (*env)->GetStringUTFChars(env, strMessage, 0);
    int message_length = (int) (*env)->GetStringUTFLength(env, strMessage);

    send(intSockFd, message, message_length, 0);
}