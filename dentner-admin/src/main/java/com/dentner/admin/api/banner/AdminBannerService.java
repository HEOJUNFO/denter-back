package com.dentner.admin.api.banner;


import com.dentner.core.cmmn.dto.BannerAddDto;
import com.dentner.core.cmmn.dto.BannerDto;
import com.dentner.core.cmmn.mapper.AdminBannerMapper;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.BannerListVo;
import com.dentner.core.cmmn.vo.BannerVo;
import com.dentner.core.cmmn.vo.S3FileVO;
import com.dentner.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBannerService {

	@Autowired
	AdminBannerMapper adminBannerMapper;

	@Autowired
	FileMapper fileMapper;

	private final S3Upload s3Upload;

	public BannerListVo getBannerList(String bannerSe, BannerDto bannerDto) {
		bannerDto.setBannerSe(bannerSe);
		BannerListVo bannerListVo = new BannerListVo();
		bannerListVo.setList(adminBannerMapper.selectBannerList(bannerDto));
		bannerListVo.setCnt(adminBannerMapper.selectBannerListCnt(bannerDto));
		return bannerListVo;
	}

	public BannerVo getBannerDetail(int bannerNo) {
		BannerVo bannerVo = new BannerVo();
		bannerVo = adminBannerMapper.selectBannerDetail(bannerNo);
		return bannerVo;
	}

	public BannerAddDto postBanner(List<MultipartFile> files, List<MultipartFile> mobFiles, BannerAddDto bannerAddDto) throws IOException{
		bannerAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		int result = adminBannerMapper.insertBanner(bannerAddDto);
		if(result > 0){
			if(files != null && files.size() > 0){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(files.get(i));
					fileVO.setFileFromNo(bannerAddDto.getBannerNo());
					fileVO.setFileSe("E");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
		}

		return bannerAddDto;
	}

	public BannerAddDto putBanner(int bannerNo, List<MultipartFile> files, List<MultipartFile> mobFiles, BannerAddDto bannerAddDto) throws IOException{
		bannerAddDto.setBannerNo(bannerNo);
		if(bannerAddDto.getFileDel() != null && !"".equals(bannerAddDto.getFileDel())){
			fileMapper.deleteFileArr(bannerAddDto.getFileDel());
		}
		int result = adminBannerMapper.updateBanner(bannerAddDto);
		if(result > 0 ){
			if(files != null && files.size() > 0){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(files.get(i));
					fileVO.setFileFromNo(bannerAddDto.getBannerNo());
					fileVO.setFileSe("E");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
		}
		return bannerAddDto;
	}

	public int deleteBanner(String bannerNoArr) {
		int result = adminBannerMapper.deleteBanner(bannerNoArr);
		return result;
	}

	public int putBannerOrdr(int bannerNo, BannerAddDto bannerAddDto) {
		bannerAddDto.setBannerNo(bannerNo);
		return adminBannerMapper.updateBannerOrdr(bannerAddDto);
	}
}
