package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.config.AppConfig;
import com.easychat.dto.MessageSendDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.ChatMessageService;
import com.easychat.service.ChatSessionUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController{
    private static final Logger logger= LoggerFactory.getLogger(ChatController.class);
    @Resource
    private ChatMessageService chatMessageService;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private AppConfig appconfig;
    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO sendMessage(HttpServletRequest request, @NotNull String contactId,
                                  @NotEmpty @Max(500) String messageContent,
                                  @NotNull Integer messageType,
                                  Long fileSize,
                                  String fileName,
                                  Integer fileType) throws BusinessException {

        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setFileType(messageType);
        chatMessage.setFileName(fileName);
        chatMessage.setMessageType(messageType);
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        MessageSendDto messageSendDto=chatMessageService.saveMessage(chatMessage,tokenUserInfoDto);
       return getSuccessResponseVO(messageSendDto);
    }
    @RequestMapping("/uploadFile")
    @GlobalInterceptor
    public ResponseVO uploadFile(HttpServletRequest request, @NotNull Long messageId, @NotNull MultipartFile file,@NotNull MultipartFile cover) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        chatMessageService.saveMessageFile(tokenUserInfoDto.getUserId(),messageId,file,cover);
    }
}
