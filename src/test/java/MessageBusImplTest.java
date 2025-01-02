

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import static org.mockito.Mockito.*;
import bgu.spl.mics.MicroService;
import org.mockito.Mock;



public class MessageBusImplTest {
    
    MessageBusImpl messageBusImpl;
    Event<Object> mockEvent;
    MicroService mockMicroService;

    @BeforeEach
    public void setUp() {
        messageBusImpl = MessageBusImpl.getInstance();
        mockEvent = mock(Event.class);
        mockMicroService = mock(MicroService.class);
    }

    @Test
    public void testSubscribeEvent() {
        // test if the event is added to the queue
        messageBusImpl.subscribeEvent((Class<? extends Event<Object>>) mockEvent.getClass(), mockMicroService);
        assertTrue(messageBusImpl.getEventsSubs().get(mockEvent.getClass()).contains(mockMicroService));
    }
   


}
