package tn.esprit.educareer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Holiday {

    private String name;
    private String description;
    private Country country;
    private Date date;
    private List<String> type;
    @JsonProperty("primary_type")
    private String primaryType;
    @JsonProperty("canonical_url")
    private String canonicalUrl;
    private String urlid;
    private String locations;
    private String states;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public String getUrlid() {
        return urlid;
    }

    public void setUrlid(String urlid) {
        this.urlid = urlid;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public static class Country {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Date {
        private String iso;
        private Datetime datetime;
        private Timezone timezone;

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public Datetime getDatetime() {
            return datetime;
        }

        public void setDatetime(Datetime datetime) {
            this.datetime = datetime;
        }

        public Timezone getTimezone() {
            return timezone;
        }

        public void setTimezone(Timezone timezone) {
            this.timezone = timezone;
        }
    }

    public static class Datetime {
        private int year;
        private int month;
        private int day;
        private Integer hour;
        private Integer minute;
        private Integer second;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getSecond() {
            return second;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }
    }

    public static class Timezone {
        private String offset;
        private String zoneabb;
        private int zoneoffset;
        private int zonedst;
        private int zonetotaloffset;

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public String getZoneabb() {
            return zoneabb;
        }

        public void setZoneabb(String zoneabb) {
            this.zoneabb = zoneabb;
        }

        public int getZoneoffset() {
            return zoneoffset;
        }

        public void setZoneoffset(int zoneoffset) {
            this.zoneoffset = zoneoffset;
        }

        public int getZonedst() {
            return zonedst;
        }

        public void setZonedst(int zonedst) {
            this.zonedst = zonedst;
        }

        public int getZonetotaloffset() {
            return zonetotaloffset;
        }

        public void setZonetotaloffset(int zonetotaloffset) {
            this.zonetotaloffset = zonetotaloffset;
        }
    }
}