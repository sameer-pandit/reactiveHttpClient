package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.reactive.ResponseExtractors;
import org.springframework.web.client.reactive.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.springframework.web.client.reactive.ClientWebRequestBuilders.get;
import static org.springframework.web.client.reactive.ClientWebRequestBuilders.post;
import static org.springframework.web.client.reactive.ResponseExtractors.body;
import static org.springframework.web.client.reactive.ResponseExtractors.bodyStream;

public class ReactiveClientApplication {

  public static void main(String[] args) throws  InterruptedException{
    // Using the Spring WebClient API.
    WebClient webClient = new WebClient(new ReactorClientHttpConnector());
    String fluxUrl = "http://localhost:8080/txPointTraditional";
    String sseUrl = "http://localhost:8080/txPointTraditionalSse";
    String sleepUrl = "http://localhost:8080/txPointGet/6/7";
    Flux<Integer> xFlux = Flux.range(3,10);
    Flux<Integer> yFlux = Flux.range(8,10);
    Flux<Coordinates> coordinatesFlux = Flux.zip(xFlux,yFlux,(x,y)->new Coordinates(x,y));
    /*Flux<MappedCoordinates> mappedCoordinates = webClient
            .perform(post(fluxUrl).body(coordinatesFlux, ResolvableType.forClass(Coordinates.class)).contentType(MediaType.APPLICATION_JSON).accept(new MediaType("text","event-stream")))
            .extract(bodyStream(MappedCoordinates.class));
    CountDownLatch latch = new CountDownLatch(1);

    mappedCoordinates.doOnComplete(()->latch.countDown()).subscribe(System.out::println);
    latch.await();*/
    
    CountDownLatch newLatch = new CountDownLatch(1);
    Flux<MappedCoordinates> mappedCoordinates = webClient
            .perform(post(fluxUrl).body(coordinatesFlux, ResolvableType.forClass(Coordinates.class)).contentType(MediaType.APPLICATION_JSON).accept(new MediaType("text","event-stream")))
            .extract(bodyStream(String.class))
            .filter(s -> !s.equals("\n"))
            .filter(s -> !s.equals("data:"))
            .map(s -> (s.replace("\n", "")))
            .map((e -> {
              try {
                MappedCoordinates a = new ObjectMapper().readValue(e, MappedCoordinates.class);
                return a;
              } catch (Exception e1) {
                e1.printStackTrace();
                return null;
              }
            }));
    mappedCoordinates.doOnComplete(()->newLatch.countDown()).subscribe(System.out::println);
    //Using the Jersey EventSource API
    /*Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
    WebTarget target = client.target(sseUrl);
    EventSource eventSource = new EventSource(target){s
      @Override
      public void onEvent(InboundEvent event){
        try {
          MappedCoordinates a = new ObjectMapper().readValue(event.readData(), MappedCoordinates.class);
          System.out.println(a);
        }catch (Exception e){

        }
        //MappedCoordinates a = event.readData(MappedCoordinates.class, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        newLatch.countDown();
      }
    };*/
    newLatch.await();
  }
}
