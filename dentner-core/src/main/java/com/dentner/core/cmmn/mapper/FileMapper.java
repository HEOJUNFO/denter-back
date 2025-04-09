package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.vo.FileVO;
import com.dentner.core.cmmn.vo.S3FileListVO;
import com.dentner.core.cmmn.vo.S3FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
	int insertFile(S3FileVO fileVO);

	int deleteFileArr(@Param(value = "fileNoArr") String fileNoArr);

	List<S3FileVO> selectFileList(@Param(value = "fileFromNo") int fileFromNo, @Param(value = "fileSe") String fileSe);

	S3FileVO selectFile(@Param(value = "fileFromNo") int fileFromNo, @Param(value = "fileSe") String fileSe);
	S3FileVO selectFileNo(@Param(value = "fileNo") Integer fileNo);

	int deleteFile(S3FileVO delFileVO);

	S3FileVO selectChatFile(@Param(value = "chatNo") Integer chatNo);

    int insertFileOrdr(S3FileVO fileVO);

	List<S3FileListVO> selectFileLists(@Param(value = "fileFromNo") int fileFromNo, @Param(value = "fileSe") String fileSe);

	List<S3FileListVO> selectFileCadList(@Param(value = "fileFromNo") int fileFromNo, @Param(value = "fileSe") String fileSe, @Param(value = "fileOrdr") int fileOrdr);

	int selectFileOrdr(@Param(value = "fileFromNo") int fileFromNo, @Param(value = "fileSe") String fileSe);
}
