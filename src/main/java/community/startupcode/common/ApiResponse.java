package community.startupcode.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{
    private boolean isSuccess=true;
    private String code=null;
    private String message="성공했습니다.";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime responseAt = OffsetDateTime.now();
    private T data = null;

    public ApiResponse(T data, String code){
        this.data=data;
        this.code=code;
    }

    public ApiResponse(T data, String code, String message){
        this.data = data;
        this.code = code;
        this.message = message;
    }

}