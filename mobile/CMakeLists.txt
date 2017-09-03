# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.c )

# OpenCV native for Android
set(OPENCV_HOME ${CMAKE_CURRENT_SOURCE_DIR}/../../OpenCV-android-sdk)
set(OpenCV_STATIC ON)
set(OpenCV_DIR ${OPENCV_HOME}/sdk/native/jni)

# Add other CMake subprojects to link against it's libraries
# subprojects paths
set(REMOTE_CONTROLLER_API_CLIENT_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../c/client )
set(REMOTE_CONTROLLER_API_CLIENT_BUILD_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../c/client/outputs )
# add subprojects
add_subdirectory(   # Specifies the directory of the CMakeLists.txt file.
                    ${REMOTE_CONTROLLER_API_CLIENT_SRC_DIR}
                    # Specifies the directory for the build outputs.
                    ${REMOTE_CONTROLLER_API_CLIENT_BUILD_DIR} )

# include header files of subproject
set(NETWORKING_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../c/networking )
set(COMMON_FUNCTIONS_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../c/common )
include_directories(${REMOTE_CONTROLLER_API_CLIENT_SRC_DIR}/include)
include_directories(${NETWORKING_SRC_DIR}/include)
include_directories(${COMMON_FUNCTIONS_SRC_DIR}/include)
include_directories(${COMMON_FUNCTIONS_SRC_DIR}/libraries)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( native-lib RC_Client_Lib )