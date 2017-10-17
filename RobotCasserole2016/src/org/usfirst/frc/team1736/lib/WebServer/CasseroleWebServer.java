package org.usfirst.frc.team1736.lib.WebServer;

/*
 *******************************************************************************************
 * Copyright (C) 2017 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
 *
 * This software is released under the MIT Licence - see the license.txt
 *  file in the root of this repo.
 *
 * Non-legally-binding statement from Team 1736:
 *  Thank you for taking the time to read through our software! We hope you
 *   find it educational and informative! 
 *  Please feel free to snag our software for your own use in whatever project
 *   you have going on right now! We'd love to be able to help out! Shoot us 
 *   any questions you may have, all our contact info should be on our website
 *   (listed above).
 *  If you happen to end up using our software to make money, that is wonderful!
 *   Robot Casserole is always looking for more sponsors, so we'd be very appreciative
 *   if you would consider donating to our club to help further STEM education.
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * DESCRIPTION: <br>
 * Basic controls for a customized Jetty embedded webserver, serving up a fixed number of useful
 * pages for displaying robot data and calibration information. <br>
 * ASSUMPTIONS: <br>
 * Be sure to use the DriverView and WebStates classes to assign content into the web pages. <br>
 * USAGE:
 * <ol>
 * <li>Instantiate class</li>
 * <li>On init, assign content to web pages.</li>
 * <li>Call startServer just before the robot enters disabled mode for the first time.</li>
 * </ol>
 * 
 *
 */

public class CasseroleWebServer {


    static Server server;


    /**
     * Starts the web server in a new thread. Should be called at the end of robot initialization.
     */
    public void startServer() {

        final boolean LOCAL_PC_DEBUG_PATHS = false;

        // New server will be on the robot's address plus port 5805
        server = new Server(5805);


        // Set up classes which will handle web requests
        // I'm not entirely certain how we'll make this work, but here's my first pass:
        // The build process has been modified to also copy the web resource files to the RIO
        // Since we're not really concerned about security, the files are all accessible.
        // the resource_handler makes the .html/.css files on the RIO available to a client to
        // access freely.
        // index.html is served by default if no other specific file is requested.
        // The ServletContextHandler holds more specific types of content that can be served up
        // directly.
        // Mostly this is the JSON data streams for displaying state data and config and stuff.
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[] {"index.html"});
        if (LOCAL_PC_DEBUG_PATHS) {
            resource_handler.setResourceBase("resources/");
        } else {
            resource_handler.setResourceBase("/home/lvuser/resources/");
        }
        server.insertHandler(resource_handler);

        // StateStreamer - broadcasts present state of robot. RPM's, voltages, etc.
        ServletHolder statestreamHolder = new ServletHolder("statestream", new CasseroleStateStreamerServlet());
        context.addServlet(statestreamHolder, "/statestream");

        // CalStreamer - Handles calibration viewing and updating
        ServletHolder calstreamHolder = new ServletHolder("calstream", new CasseroleCalStreamerServlet());
        context.addServlet(calstreamHolder, "/calstream");

        // CalStreamer - Handles calibration viewing and updating
        ServletHolder driverDatstreamHolder =
                new ServletHolder("driverviewstream", new CasseroleDriverViewStreamerServlet());
        context.addServlet(driverDatstreamHolder, "/driverviewstream");
        
        // RT Plot Streamer - broadcasts things which can be plotted in real-time
        ServletHolder rtPlotHolder = new ServletHolder("rtplotstream", new CasseroleRTPlotStreamerServlet());
        context.addServlet(rtPlotHolder, "/rtplot");

        // Kick off server in brand new thread.
        // Thanks to Team 254 for an example of how to do this!
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        serverThread.setName("CasseroleWebServerThread");
        serverThread.setPriority(Thread.MIN_PRIORITY);
        serverThread.start();

    }

}
