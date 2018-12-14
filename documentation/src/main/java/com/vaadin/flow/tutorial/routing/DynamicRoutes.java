/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.tutorial.routing;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.DynamicRoute;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.internal.AbstractRouteRegistry;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.SessionRouteRegistry;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.tutorial.annotations.CodeFor;

@CodeFor("routing/tutorial-router-dynamic-routes.asciidoc")
public class DynamicRoutes {

    @Route("main")
    @RouteAlias("info")
    @RouteAlias("version")
    private class MyRoute extends Div {

        public MyRoute() {
            RouteRegistry routeRegistry = new AbstractRouteRegistry() {

                @Override
                public Optional<Class<? extends Component>> getNavigationTarget(
                        String pathString) {
                    return Optional.empty();
                }

                @Override
                public Optional<Class<? extends Component>> getNavigationTarget(
                        String pathString, List<String> segments) {
                    return Optional.empty();
                }
            };
            routeRegistry.setRoute("main", MyRoute.class, Collections.emptyList());
            routeRegistry.setRoute("info", MyRoute.class, Collections.emptyList());
            routeRegistry.setRoute("version", MyRoute.class, Collections.emptyList());
            // No path "users" should be available
            routeRegistry.removeRoute("users");

            // No navigationTarget Users should be available
            routeRegistry.removeRoute(Users.class);

            // Only the Users navigationTarget should be removed from "users"
            routeRegistry.removeRoute("users", Users.class);

            List<Class<? extends RouterLayout>> parentLayouts = Arrays.asList(MainLayout.class);
            routeRegistry.setRoute("home", Home.class, parentLayouts);

            VaadinSession session = VaadinSession.getCurrent();
            RouteRegistry sessionRegistry = SessionRouteRegistry.getSessionRegistry(session);

            ServletContext servletContext = VaadinServlet.getCurrent().getServletContext();
            RouteRegistry registry = ApplicationRouteRegistry.getInstance(servletContext);
        }
    }

    @Route("")
    @DynamicRoute
    public class Admin extends Div {
    }

    public class User extends Div {
    }

    private static class Home extends Div {
    }
    private static class Users extends Div {
    }
    private static class MainLayout extends Div implements RouterLayout {
    }

    @Route("")
    public class Login extends Div {

        private TextField login;
        private PasswordField password;

        public Login() {
            login = new TextField("Login");
            password = new PasswordField("Password");

            Button submit = new Button("Submit", this::handeLogin);

            add(login, password, submit);
        }

        private void handeLogin(ClickEvent<Button> buttonClickEvent) {
            // Validation of credentials is skipped

            RouteRegistry sessionRegistry = SessionRouteRegistry
                    .getSessionRegistry(VaadinSession.getCurrent());

            if ("admin".equals(login.getValue())) {
                sessionRegistry.setRoute("", Admin.class, Collections.emptyList());
                RouteUtil.setAnnotatedRoute(Admin.class, sessionRegistry);
            } else if ("user".equals(login.getValue())) {
                sessionRegistry.setRoute("", User.class, Collections.emptyList());

                UI.getCurrent().getPage().reload();
            }

        }

    }

    @Route("info")
    public class Info extends Div {
        public Info() {
            add(new Span("This page contains info about the application"));
        }
    }
}