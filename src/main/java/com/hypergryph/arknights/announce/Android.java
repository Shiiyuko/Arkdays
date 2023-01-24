/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.announce.Android
 *  com.hypergryph.arknights.core.file.IOTools
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.announce;

import com.hypergryph.arknights.core.file.IOTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/announce"})
public class Android {
    @RequestMapping(value={"/Android/preannouncement/280_1618473718.html"})
    public String PreAnnouncement() {
        return IOTools.ReadNormalFile((String)(System.getProperty((String)"user.dir") + "/data/static/announcement/280_1618473718.html"));
    }

    @RequestMapping(value={"/Android/css/preannouncement.v_0_1_2.css"})
    public String PreAnnouncementCss() {
        return IOTools.ReadNormalFile((String)(System.getProperty((String)"user.dir") + "/data/static/css/preannouncement.v_0_1_2.css"));
    }
}

