package com.example.demo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 파일 관련 에러
    INVALID_FILE(HttpStatus.BAD_REQUEST, "유효하지 않은 파일입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 10MB를 초과했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 확장자입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),

    // 건의사항 관련 에러
    SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "건의사항을 찾을 수 없습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 채팅방 관련 에러
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_FULL(HttpStatus.BAD_REQUEST, "채팅방이 가득 찼습니다."),
    CHAT_ROOM_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),
    NOTICE_POST_ONLY_ADMIN(HttpStatus.FORBIDDEN, "공지사항은 관리자만 작성할 수 있습니다."),

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다."),

    // 투표 관련 에러
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "투표를 찾을 수 없습니다."),
    VOTE_CLOSED(HttpStatus.BAD_REQUEST, "종료된 투표입니다."),
    VOTE_EXPIRED(HttpStatus.BAD_REQUEST, "마감된 투표입니다."),
    VOTE_SINGLE_CHOICE_ONLY(HttpStatus.BAD_REQUEST, "단일 선택 투표입니다."),
    VOTE_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "선택지를 찾을 수 없습니다."),
    VOTE_OPTION_MIN_COUNT(HttpStatus.BAD_REQUEST, "최소 2개의 선택지가 필요합니다."),
    VOTE_OPTION_MAX_COUNT(HttpStatus.BAD_REQUEST, "선택지는 최대 10개까지 가능합니다."),
    VOTE_OPTION_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 선택지가 있습니다."),
    VOTE_OPTION_HAS_VOTES(HttpStatus.BAD_REQUEST, "이미 투표가 진행된 선택지는 삭제할 수 없습니다."),
    VOTE_ADD_OPTION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "이 투표는 선택지 추가가 허용되지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
