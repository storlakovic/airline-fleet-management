package com.storlakovic.airlineoperationssimulator.route;


import com.storlakovic.airlineoperationssimulator.route.dto.RouteCreateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public Route createRoute(@RequestBody RouteCreateRequest request) {
       return routeService.addRoute(request);
    }
}
