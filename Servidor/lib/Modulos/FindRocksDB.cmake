find_path(ROCKSDB_INCLUDE_DIRS db.h "${ROCKSDB_ROOT_DIR}")

find_library(ROCKSDB_LIBRARIES rocksdb "${LIBS_ROOT_DIR}")

set(ROCKSDB_FOUND TRUE)

if (NOT ROCKSDB_INCLUDE_DIRS)
  set(ROCKSDB_FOUND FALSE)
endif (NOT ROCKSDB_INCLUDE_DIRS)

if (NOT ROCKSDB_LIBRARIES)
  set(ROCKSDB_FOUND FALSE)
endif (NOT ROCKSDB_LIBRARIES)
