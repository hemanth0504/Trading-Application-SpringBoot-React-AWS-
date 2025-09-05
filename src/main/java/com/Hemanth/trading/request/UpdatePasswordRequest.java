package com.Hemanth.trading.request;

import com.Hemanth.trading.domain.VerificationType;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String sendTo;
    private VerificationType verificationType;
}
