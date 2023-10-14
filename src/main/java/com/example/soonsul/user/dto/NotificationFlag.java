package com.example.soonsul.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "알림 유무")
public class NotificationFlag {

    @ApiModelProperty(value = "활동 알림 허용 유무", position = 1)
    private boolean flagActivity;

    @ApiModelProperty(value = "광고성 알림 허용 유무", position = 2)
    private boolean flagAdvertising;
}
