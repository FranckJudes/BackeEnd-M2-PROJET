package com.harmony.harmoniservices.core.ports.incoming;

import com.harmony.harmoniservices.core.domain.events.UserEvent;

public interface UserEventConsumer {
    
    void handleUserEvent(UserEvent event);
}
