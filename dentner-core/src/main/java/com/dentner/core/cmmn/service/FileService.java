package com.dentner.core.cmmn.service;


import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.vo.S3FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("fileService")
public class FileService {

    @Autowired
    FileMapper fileMapper;

    public S3FileVO selectFile(Integer fileNo) throws Exception{
        return fileMapper.selectFileNo(fileNo);
    }

    public List<S3FileVO> selectFiles(Integer fileFormNo, String fileSe) throws Exception{
        return fileMapper.selectFileList(fileFormNo, fileSe);
    }

    public S3FileVO selectChatFile(Integer chatNo) {
        return fileMapper.selectChatFile(chatNo);
    }
}
