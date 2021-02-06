package com.kctv.api.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ReferRequest {
        private UUID recvUserId;
        private UUID sendUserId;
        private String addVolume;


        public ReferRequest(UUID sendUserId, UUID recvUserId, String addVolume) {
                this.recvUserId = recvUserId;
                this.sendUserId = sendUserId;
                this.addVolume = addVolume;
        }
}
