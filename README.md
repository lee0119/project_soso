# 📝Soso
<br>

[노션 개발일지](https://rhetorical-durian-6e6.notion.site/73948f6e42834d25bc398fa3b8495fa6?v=a25123e01c9e4b49bc1fcca129261ec0)

---

### 📌 프로젝트 소개
- 소소하지만 평범하지 않은 우리들의 일상을 공유하는 공간입니다!

<br>

### 📰 제작기간 & 팀원 소개
- 2022-09-02 ~ 2022-09-08

- 이윤재 : 프론트엔드, 메인페이지 & 게시글 관련 API 통신 및 view
- 이희수 : 프론트엔드, 상세페이지 & 댓글, 좋아요 관련 API통신 및 view
- 이주현 : 백엔드, JWT를 적용한 로그인, 회원가입 기능
- 김동훈 : 백엔드, 댓글과 좋아요 기능
- 강민승 : 백엔드, 게시글 조회, 작성, 수정, 삭제 기능

<br>

### ⛏ 사용 기술

`Back-end`
-   Java 11
-   Spring Boot 2.7.2
-   H2, MySQL
-   Spring security, JWT
-   AWS S3, IAM, EC2

`Front-end`

-   JavaScript
-   React

<br>

### ✔ 구현 기능

-   전체 게시글 목록 조회 (이미지, 좋아요)
-   게시글 작성 (제목, 이미지 업로드)
-   게시글 상세조회 (제목, 이미지, 댓글, 좋아요)
-   게시글 수정 및 삭제 기능

<br>

### 🎵 API

<br>
<img width="520" alt="api 11" src="https://user-images.githubusercontent.com/110277186/189059190-8941e4c3-bdb4-4262-8f57-b96349b78654.png">
<img width="518" alt="api 22" src="https://user-images.githubusercontent.com/110277186/189059197-8feb39eb-47e0-4b70-9a4c-860d7583ecbd.png">
<img width="526" alt="api 3333" src="https://user-images.githubusercontent.com/110277186/189059688-28a271f5-8f35-4726-8577-28070b52728a.png">




<br>

### ⚒ Trouble Shooting

1. 게시글 등록시 이미지 업로드 되게 하기

	- 게시글을 등록할 때 제목을 입력하고 이미지를 등록할 수 있도록 하려고 했다.
	- 문제1 : POSTMAN으로 API 테스트를 할 때,  제목은 content-type이 json이고 이미지는 multipartFile이기 때문에 기존 body에 raw데이터를 json으로 입력하는 것은 불가능했다.
		- 구글링한 결과 PostMapping 어노테이션에 'consumes' 라는 요소로 type 2가지를 명시해주고, 파라미터는 @RequestPart라는 것을 사용하였다.
		```java
		// 게시글 등록  
		@PostMapping(value = "/api/auth/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})  
		public PostResponseDto createPost(@RequestPart PostRequestDto postRequestDto, @RequestPart(required = false) MultipartFile multipartFile) throws IOException {  
		  
		    return postService.createPost(postRequestDto, multipartFile);  
		  
		}
		``` 
		- API테스트 할 때는 아래와 같이 form-data로 content-type를 나눠서 보낸다.
		![api테스트](https://user-images.githubusercontent.com/93110733/188308424-3f28dfa3-0f03-4204-b9fb-a99ca929f3a7.JPG)

	- 문제2 : entity에서 imgUrl을 nullable=true로 설정하였는데도 이미지가 업로드 되지 않으면 파일을 변환할 수 없다는 오류가 생겼다. 
		- multipartFile이 null이어도 S3Uploader에서 파일을 변환하여 업로드하는 작업은 진행되는 것 같았고 null인 파일을 변환할 수 없으니 오류가 생긴 것 같았다.
		```java
		// 1. MultipartFile을 전달받아 File로 전환한 후에 S3에 업로드  
		public String upload(MultipartFile multipartFile, String dirName) throws IOException {  
		    if(!multipartFile.isEmpty()) {  
		        isImage(multipartFile);  
		    } else return null;  
		  
		    File uploadFile = convert(multipartFile)  
		            .orElseThrow(() -> new IllegalArgumentException("파일 변환에 실패하였습니다."));  
		  
		    return upload(uploadFile, dirName);  
		}
		```
		- multipartFile이 비어있으면 null값을 반환하고 파일을 변환하는 작업은 거치지 않도록 return 하였다.


2. 게시글 삭제시 이미지도 S3에서 삭제되게 하기

	- 문제점 : 게시글을 삭제해도 S3 저장소에는 여전히 이미지 파일이 남아있어서 URL만 알면 접근이 가능했고, 삭제된 게시글의 이미지는 저장소에서 의미없는 용량을 차지하고 있기에 삭제가 필요하였다.
	- S3 저장소의 파일을 삭제할 때 필요한 요소는 객체의 Key값이며 Key값은 객체의 URL에서 버킷주소를 제외한 값이다.(뭐래는 거임)
		- 객체 URL 예시
			- https://postblog-bucket.s3.ap-northeast-2.amazonaws.com/static/66baf610-cf7a-46fc-a54f-420fca739841notice.jpg
			- URL은 업로드를 진행하면서 다음과 같이 바뀐다.
				- bucket주소/폴더경로(dirName)/UUID값+파일이름
				- static은 폴더경로이고 그 뒷부분이 UUID값과 파일이름인 notice.jpg이다.
		 - 객체의 Key값
			 - 여기서 객체의 Key값은 bucket주소를 제외한 부분이다.
			 - Key값 : static/66baf610-cf7a-46fc-a54f-420fca739841notice.jpg
	- 파일을 업로드하는 과정에서 string fileName이라며 Key값이 만들어지는데 이 값을 어떻게 불러와야할지 고민되었다.
	```java
	// 2. S3에 파일 업로드 하기  
	//    fileName = S3에 저장되는 파일이름(randomUUID는 파일이 덮어씌워지지 않기 위함)  
	//    1번을 진행하면서 로컬에 생성된 파일을 삭제까지 하는 프로세스  
	private String upload(File uploadFile, String dirName) {  
	    String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();  
	    String uploadImageUrl = putS3(uploadFile, fileName);  
	  
	    removeNewFile(uploadFile);  
	  
	    return uploadImageUrl;  
	}
	```
	- PostRepository를 필드에 불러온 후에 postRepostory에 fileName을 save하는 방법을 해보았지만, 그러면 정작 PostService에서 title과 imgUrl값이 저장이 되지 않았다.

	- 다음은 게시글 등록처리 하는 과정이다. 이 부분에서 postRepository에 title, imgUrl과 함께 fileName(key값)이 저장이 되어야 삭제를 할 때 키 값을 불러올 수 있을 것 같았다.
	```java
	// 게시글 등록 처리  
	@Transactional  
	public PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile multipartFile) throws IOException {  
	  
	    String imgUrl = s3Uploader.upload(multipartFile, "soso");  
	    String fileName;  
	    if(imgUrl == null) {  
	        fileName = null;  
	    } else {  
	        fileName = imgUrl.substring(imgUrl.indexOf("soso"));  
	    }  
	    Post post = Post.builder()  
	            .title(postRequestDto.getTitle())  
	            .fileName(fileName)  
	            .imgUrl(imgUrl)  
	            .build();  
	    postRepository.save(post);  
	  
	    return PostResponseDto.builder()  
	            .id(post.getId())  
	            .title(post.getTitle())  
	            .imgUrl(post.getImgUrl())  
	            .createdAt(post.getCreatedAt())  
	            .modifiedAt(post.getModifiedAt())  
	            .build();  
	}
	```


	


	- 위에서도 설명했듯이 이미지URL에 bucket주소를 제외해야 하므로, substring을 이용하여 폴더경로 전까지 자르고 fileName이라는 변수에 저장하였다. 이 때, Key값대로 값은 잘 저장이 되었으나 이미지를 업로드하지 않을 경우 imgURL이 null인데 null을 substring할 수가 없으니 오류가 생겼다.
	- 그래서 imgUrl이 null값이면 fileName도 null을 주고, imgUrl이 있을 경우 그 url을 substring해서 key값을 얻을 수 있도록 하였다.


