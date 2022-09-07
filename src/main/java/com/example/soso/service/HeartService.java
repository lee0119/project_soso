package com.example.soso.service;

import javax.servlet.http.HttpServletRequest;
import com.example.soso.domain.Heart;
import com.example.soso.domain.Member;
import com.example.soso.domain.Post;
import com.example.soso.dto.response.ResponseDto;
import com.example.soso.jwt.TokenProvider;
import com.example.soso.repository.HeartRepository;
import com.example.soso.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HeartService {


    private final PostRepository postRepository;
    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> postLike(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

//        Optional<Heart> heartList = heartRepository.findByPostId(postId);
        if(heartRepository.findByPostId(postId).isEmpty()){
            Heart heart = Heart.builder()
                    .postId(postId)
                    .member(member)
                    .flag(true)
                    .build();

            post.like();

            heartRepository.save(heart);
        }else{
            return ResponseDto.fail("already like", "already like state");
        }
        return ResponseDto.success("like success");
    }

    @Transactional
    public ResponseDto<?> postDisLike(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        Heart heart = isPresentPostLike(postId);
        if (heart.validateMember(member)) {
            return ResponseDto.fail("dislike fail", "좋아요 작성자가 아닙니다.");
        }

//        Optional<PostLike> postLikeList = postLikeRepository.findByPostId(postId);
        if(heart.isFlag() == false){
            return ResponseDto.fail("already dislike", "already dislike state");
        }else{
            heart.setFlag(false);
            post.dislike();
            heartRepository.delete(heart);
        }
        return ResponseDto.success("dislike success");
    }



    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public Heart isPresentPostLike(Long id) {
        Optional<Heart> optionalHeart = heartRepository.findById(id);
        return optionalHeart.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
