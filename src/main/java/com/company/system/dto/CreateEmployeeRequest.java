package com.company.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 社員作成リクエストDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "名前は必須です")
    @Size(max = 100, message = "名前は100文字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    @NotNull(message = "部署IDは必須です")
    private Long departmentId;

    @NotNull(message = "入社日は必須です")
    private LocalDate joinDate;
}


