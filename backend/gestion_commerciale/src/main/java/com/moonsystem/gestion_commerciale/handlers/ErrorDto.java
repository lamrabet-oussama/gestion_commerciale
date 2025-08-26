package com.moonsystem.gestion_commerciale.handlers;


import java.util.ArrayList;
import java.util.List;

import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDto {

    private Integer httpCode;

    private ErrorCodes code;

    private String message;

    private List<String> errors = new ArrayList<>();

}
