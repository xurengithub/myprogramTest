package com.xuren.service.impl;

import java.util.Date;
import java.util.List;

import com.xuren.mapper.VideosMapper;
import com.xuren.pojo.Videos;
import com.xuren.service.VideoService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videosMapper;

	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public String saveVideo(Videos video) {
		String id = sid.nextShort();
		video.setId(id);
		videosMapper.insertSelective(video);

		return id;
	}
}
