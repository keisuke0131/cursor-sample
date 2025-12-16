package com.company.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 社員更新リクエストDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "名前は100文字以内で入力してください")
    private String name;

    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    private Long departmentId;
}


