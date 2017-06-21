FORMAT: 1A

HOST: http://54.254.97.135/wynkstudio/

# Wynk Middleware APIs
API endpoints for Wynk Studio Middleware

# Group AppGrid
AppGrid end points for Wynk Studio

## Session [/session?appKey={appKey}&uuid={deviceID}]
+ Parameters
    + appKey (string) ... AppKey for appgrid instance
    + uuid (string) ... UUID of the device

+ Model (application/json)
    
    + Body
        {
            "sessionKey": "4f58ec96bb7c4fc0f42dbdc81a502c0f31a10206",
            "expiration": "20141109T17:46:26+0000"
        }

### Retrieve AppGrid Session [GET]
+ Response 200
    [Session][]

## Status [/status]
+ Parameters


+ Model (application/json)

    + Headers
        X-Session: {sessionKey} ... Sessionkey from Appgrid

    + Body
        {
            "staus": "Active"
        }

### Check session status [GET]
+ Response 200
    [Status][]

## Assets [/asset]
+ Parameters

+ Model (application/json)
    
    + Headers
        X-Session: {sessionKey} ... Sessionkey from Appgrid

    + Body
        {
        "asset_img_menu_signIn": "/asset/5523dfe51703cca1ee44bb00d64dce6b580660dfef24cc6f",
        "asset_setting_color_blue": "/asset/55430f4517b3fc61ee44bb00767dcebb28a6c0afaf54ec2f",
        "asset_setting_language_english": "/asset/5533ff55c713cc11ee44bb0066ddeebb886600ffff544c4f",
        "asset_img_menu_home": "/asset/5523dfe51703ece1ee44bb00d64dce6b580660dfef24dcef",
        "asset_img_logo_small": "/asset/55334f5507e39cb1ee44bb00d63d1eaba8e6601fcfa4cccf",
        "asset_img_menu_connect": "/asset/5523dfe51703fca1ee44bb00d64dce6b580660dfef24ec4f",
        "asset_setting_language_french": "/asset/55430f554783cce1ee44bb00767dcebb28a6c0afaf842ccf",
        "asset_img_menu_watchHistory": "/asset/55433fe507d3cc31ee44bb00c63d6eabc8c6c03f1f94bcbf",
        "asset_setting_color_pink": "/asset/5533ff5597630cd1ee44bb0066ddeebb886600ffff44fc3f",
        "asset_img_logo": "/asset/5523dfe517136cc1ee44bb00d64dce6b580660dfef340c6f",
        "asset_img_menu_tvListing": "/asset/5523dfe51703cc11ee44bb00d64dce6b580660dfef24cc0f",
        "asset_img_menu_favorite": "/asset/5523dfe51703fc41ee44bb00d64dce6b580660dfef24ec0f",
        "asset_img_menu_movie": "/asset/5523dfe51703ec81ee44bb00d64dce6b580660dfef24dcaf",
        "asset_img_menu_rental": "/asset/5523dfe51703dcb1ee44bb00d64dce6b580660dfef24dc0f",
        "asset_img_promotion_banner_4": "/asset/55332f9547f36c61ee44bb00765d8e8b8846308f1f740c2f",
        "asset_setting_language_spanish": "/asset/5533ff55c713ccc1ee44bb0066ddeebb886600ffff544caf",
        "asset_img_menu_onNow": "/asset/5523dfe51703ec11ee44bb00d64dce6b580660dfef24dc4f",
        "asset_img_menu_tvShow": "/asset/5523dfe51703bc81ee44bb00d64dce6b580660dfef24bcef",
        "asset_img_promotion_banner_1": "/asset/55332f9547f32c61ee44bb00765d8e8b8846308f1f64fc9f",
        "asset_img_promotion_banner_3": "/asset/55332f9547f34cb1ee44bb00765d8e8b8846308f1f64fcef",
        "asset_img_menu_setting": "/asset/5523dfe51703dc21ee44bb00d64dce6b580660dfef24cccf",
        "asset_img_promotion_banner_2": "/asset/55332f9547f33cd1ee44bb00765d8e8b8846308f1f64fcbf"
        }

### Retrieve AppGrid Assets [GET]
+ Response 200
    [Assets][]

## Asset [/asset/{id}]
+ Parameters

+ Model (application/json)
    
    + Headers
        X-Session: {sessionKey} ... Sessionkey from Appgrid

    + Body
       {
        "generalIcon": [255, 255, 255],
        "generalText": [255, 255, 255],
        "secondLevelText": [255, 255, 255],
        "highlightedText": [0, 155, 221],
        "menuItemText": [255, 255, 255],
        "background": [61, 71, 83],
        "popupBackground": [61, 71, 83],
        "selected": [0, 155, 221],
        "transparentSelected": [21, 52, 82, 0.95],
        "menuBackground": [18, 32, 43],
        "expandedMenuItemBackground": [27, 46, 61],
        "submenuBackground": [7, 21, 30],
        "buttonBackground": [54, 54, 54],
        "lightButtonBackground": [184, 184, 184],
        "buttonText": [255, 255, 255],
        "programGuideTimePointer": [0, 155, 221],
        "programGuideTimelineBackground": [0, 0, 0],
        "programGuideTimelineText": [255, 255, 255],
        "programGuideChannel": [37, 35, 35],
        "programGuideProgramOnNowBackground": [83, 90, 98, 0.4],
        "programGuideProgramBackground": [83, 90, 98, 0.75]
        }

### Retrieve AppGrid Asset by Id [GET]
+ Response 200
    [Asset][]

## Metadata [/metadata]
+ Parameters


+ Model (application/json)
    
    + Headers
        X-Session: {sessionKey} ... Sessionkey from Appgrid

    + Body
      {
          "menu": [
            {
              "title": {
                "en_US": "Home",
                "es_ES": "Casa"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "at_home"
              },
              "action": "home",
              "actionID": "home",
              "visibility": {
                "visible": true,
                "condition": "none"
              },
              "children": []
            },
            {
              "title": {
                "en_US": "DailyMotion",
                "es_ES": "Casa"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "daily_motion"
              },
              "action": "home",
              "actionID": "DAILYMOTION",
              "visibility": {
                "visible": true,
                "condition": "none"
              },
              "children": []
            },
            {
              "title": {
                "en_US": "My Favourites",
                "es_ES": "Casa"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "asset_img_menu_favorite"
              },
              "action": "favourites",
              "actionID": "favourites",
              "visibility": {
                "visible": false,
                "condition": "LoggedIn"
              },
              "children": []
            },
            {
              "title": {
                "en_US": "My Downloads",
                "es_ES": "Casa"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "AppGrid_Key"
              },
              "action": "downloads",
              "actionID": "downloads",
              "visibility": {
                "visible": false,
                "condition": "LoggedIn"
              },
              "children": []
            },
            {
              "title": {
                "en_US": "Settings",
                "es_ES": "Casa"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "AppGrid_Key"
              },
              "action": "settings",
              "actionID": "settings",
              "visibility": {
                "visible": false,
                "condition": "None"
              },
              "children": []
            }
          ],
          "pages": [
            {
              "id": "home",
              "themeID": "default",
              "title": {
                "en_US": "HOME",
                "es_ES": "CASA"
              },
              "icon": {
                "asset_name": "logo_airtel",
                "asset_key": "AppGrid_Key"
              },
              "items": [
                {
                  "title": {
                    "en_US": "DailyMotion",
                    "es_ES": "Continuar Watching"
                  },
                  "categoryID": "Music",
                  "itemType": "Movie",
                  "itemCount": 10,
                  "cpID": "DAILYMOTION"
                }
              ],
              "showcase": {
                "categoryID": "Music",
                "itemType": "Video",
                "cpID": "DAILYMOTION",
                "itemCount": 4
              }
            },
            {
              "id": "erosnow",
              "themeID": "default",
              "title": {
                "en_US": "HOME",
                "es_ES": "CASA"
              },
              "icon": {
                "asset_name": "Resource_Name",
                "asset_key": "AppGrid_Key"
              },
              "items": [
                {
                  "title": {
                    "en_US": "Continue Watching",
                    "es_ES": "Continuar Watching"
                  },
                  "categoryID": "auto",
                  "itemType": "Movie",
                  "itemCount": 10,
                  "cpID": "DAILYMOTION"
                }
              ],
              "showcase": {
                "categoryID": "auto",
                "itemType": "Video",
                "cpID": "DAILYMOTION",
                "itemCount": 4
              }
            },
            {
              "id": "DAILYMOTION",
              "themeID": "default",
              "title": {
                "en_US": "DailyMotion",
                "es_ES": "CASA"
              },
              "icon": {
                "asset_name": "dm_logo_white",
                "asset_key": "AppGrid_Key"
              },
              "items": [
                {
                  "title": {
                    "en_US": "Music",
                    "es_ES": "Continuar Watching"
                  },
                  "categoryID": "music",
                  "itemType": "Video",
                  "itemCount": 10,
                  "cpID": "DAILYMOTION"
                },
                {
                  "title": {
                    "en_US": "News",
                    "es_ES": "Continuar Watching"
                  },
                  "categoryID": "news",
                  "itemType": "Video",
                  "itemCount": 10,
                  "cpID": "DAILYMOTION"
                },
                {
                  "title": {
                    "en_US": "Fun",
                    "es_ES": "Continuar Watching"
                  },
                  "categoryID": "fun",
                  "itemType": "Video",
                  "itemCount": 10,
                  "cpID": "DAILYMOTION"
                }
              ],
              "showcase": {
                "categoryID": "auto",
                "itemType": "Video",
                "cpID": "DAILYMOTION",
                "itemCount": 4
              }
            }
          ],
          "languages": [
            {
              "identifier": "en_US",
              "display_value": "English",
              "default": true
            },
            {
              "identifier": "hi",
              "display_value": "Hindi",
              "default": false
            }
          ],
          "themes": [
            {
              "id": "default",
              "icon": {
                "asset_key": "key"
              },
              "colors": {
                "nav_bar": "#DC143C",
                "nav_bar_underline": "#DC143C",
                "button": "#DC143C",
                "title": "#DC143C",
                "subtitle": "#DC143C",
                "rating": "#DC143C",
                "see_all_button": "#DC143C"
              }
            },
            {
              "id": "erosnow",
              "icon": {
                "asset_key": "key"
              },
              "colors": {
                "nav_bar": "#DC143C",
                "nav_bar_underline": "#DC143C",
                "button": "#DC143C",
                "title": "#DC143C",
                "subtitle": "#DC143C",
                "rating": "#DC143C",
                "see_all_button": "#DC143C"
              }
            }
          ],
          "content_providers": [
            {
              "id": "DAILYMOTION",
              "name": "Dailymotion"
            },
            {
              "id": "ErosNow",
              "name": "Eros Now"
            },
            {
              "id": "SonyLiv",
              "name": "Sony Liv"
            }
          ]
        }

### Retrieve AppGrid Metadata [GET]
+ Response 200
    [Metadata][]

+ Response 401 (application/json)

    {
      "error": {
      "status": "401 UNAUTHORIZED",
      "message": "Please specify a valid session key, either as a parameter or header.",
      "code": "401"
      }
    }

# Group Feeds
Catalogue Feeds of Wynk Studio

## Program [/feeds/{cpToken}/program/{id}]
A Program can be of type:

- series
- episode
- movies

+ Parameters
    + id (string) ... Unique ID of the program
    + cpToken (string) ... Unique token of the Content Provider 

+ Model (application/json)

    + Body
            {
  "url": "http://v12.cat.api.test.streamco.com.au/programs/0697695.json",
  "seriesUrl": "http://v12.cat.api.test.streamco.com.au/programs/0098904.json",
  "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103854630585",
  "guid": "0697695",
  "title": "The Finale",
  "shortTitle": "",
  "description": "After George and Jerry land a production deal with NBC, the four head out for Paris on NBC's private plane and are waylaid in a small Massachusetts town.",
  "shortDescription": "",
  "added": 1414532373000,
  "updated": 1414532373000,
  "programType": "episode",
  "images": {},
  "classifications": [
    {
      "scheme": "",
      "rating": "PG",
      "consumerAdvice": []
    }
  ],
  "credits": {
    "cast": [
      {
        "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
        "characterName": "",
        "creditType": "cast",
        "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
        "personName": "Jerry Seinfeld"
      },
      {
        "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
        "characterName": "",
        "creditType": "cast",
        "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
        "personName": "Julia Louis-Dreyfus"
      },
      {
        "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
        "characterName": "",
        "creditType": "cast",
        "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
        "personName": "Michael Richards"
      }
    ]
  },
  "pricingType": "PPV",
  "pricing": {
        "watchnow": {
            "id": "1234s4",
            "price": {
              "INR": 50,
              "USD": 0.99
              }
            },
        "watchlater": {
            "id": "453421",
            "price": {
              "INR": 20,
              "USD": 0.4
              }
            },
        "pack": {
            "id": "451232",
            "price": {
              "INR": 100,
              "USD": 2
              }
            }
    },
  "tags": null,
  "runtime": 0,
  "releaseYear": 1998,
  "languages": [],
  "highDefinition": false,
  "closedCaptions": null,
  "audioLayout": "",
  "new": false,
  "exclusive": false,
  "countries": null,
  "seriesId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Program/guid/2463874319/0098904",
  "tvSeasonEpisodeNumber": 22,
  "tvSeasonNumber": 9,
  "expirationDate": 1446067200000,
  "seriesPremiere": "1998-01-01",
  "seriesFinale": ""
}

### Retrieve a Single Program [GET]
+ Response 200

    [Program][]

## Season [/feeds/{cpToken}/season/{id}]
A Season is a collection of multiple episodes( Programs).

Season object will have it's own pricing information and will have to be treated as a separate entity.

+ Parameters
    + id (string) ... Unique ID of the program
    + cpToken (string) ... Unique token of the Content Provider 

+ Model (application/json)

        {
          "entries": [
            {
              "added": 1414532373000,
              "audioLayout": "",
              "classifications": [
                {
                  "consumerAdvice": [],
                  "rating": "PG",
                  "scheme": ""
                }
              ],
              "closedCaptions": null,
              "countries": null,
              "credits": {
                "cast": [
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
                    "personName": "Jerry Seinfeld",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld"
                  },
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
                    "personName": "Julia Louis-Dreyfus",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus"
                  },
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
                    "personName": "Michael Richards",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards"
                  }
                ]
              },
              "description": "After George and Jerry land a production deal with NBC, the four head out for Paris on NBC's private plane and are waylaid in a small Massachusetts town.",
              "exclusive": false,
              "expirationDate": 1446067200000,
              "guid": "0697695",
              "highDefinition": false,
              "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103854630585",
              "images": {},
              "languages": [],
              "new": false,
              "programType": "episode",
              "releaseYear": 1998,
              "duration": 0,
              "seriesFinale": "",
              "seriesId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Program/guid/2463874319/0098904",
              "seriesPremiere": "1998-01-01",
              "seriesUrl": "http://v12.cat.api.test.streamco.com.au/programs/0098904.json",
              "shortDescription": "",
              "shortTitle": "",
              "tags": null,
              "title": "The Finale",
              "tvSeasonEpisodeNumber": 22,
              "tvSeasonNumber": 9,
              "updated": 1414532373000,
              "url": "http://v12.cat.api.test.streamco.com.au/programs/0697695.json"
            },
            {
              "added": 1414532370000,
              "audioLayout": "",
              "classifications": [
                {
                  "consumerAdvice": [],
                  "rating": "PG",
                  "scheme": ""
                }
              ],
              "closedCaptions": null,
              "countries": null,
              "credits": {
                "cast": [
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
                    "personName": "Jerry Seinfeld",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld"
                  },
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
                    "personName": "Julia Louis-Dreyfus",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus"
                  },
                  {
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
                    "personName": "Michael Richards",
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards"
                  }
                ]
              },
              "description": "Just as the four are about to go to the movies, Jerry looks back on the past nine years with the audience.",
              "exclusive": false,
              "expirationDate": 1446067200000,
              "guid": "0893212",
              "highDefinition": false,
              "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103854630583",
              "images": {},
              "languages": [],
              "new": false,
              "next": "http://v12.cat.api.test.streamco.com.au/programs/0697695.json",
              "programType": "episode",
              "releaseYear": 1998,
              "duration": 0,
              "seriesFinale": "",
              "seriesId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Program/guid/2463874319/0098904",
              "seriesPremiere": "1998-01-01",
              "seriesUrl": "http://v12.cat.api.test.streamco.com.au/programs/0098904.json",
              "shortDescription": "",
              "shortTitle": "",
              "tags": null,
              "title": "The Chronicle",
              "tvSeasonEpisodeNumber": 21,
              "tvSeasonNumber": 9,
              "updated": 1414532370000,
              "url": "http://v12.cat.api.test.streamco.com.au/programs/0893212.json"
            }
          ],
              "pricingType": "PPV",
              "pricing": {
                "watchnow": {
                  "id": "1234s4",
                  "price": {
                    "INR": 50,
                    "USD": 0.99
                  }
                },
                "watchlater": {
                  "id": "453421",
                  "price": {
                    "INR": 20,
                    "USD": 0.4
                  }
                },
                "pack": {
                  "id": "451232",
                  "price": {
                    "INR": 100,
                    "USD": 2
                  }
                }
              },
          "guid": "0098904009",
          "releaseYear": 1998,
          "seasonNumber": 9,
          "title": "Season 9",
          "total": 2,
          "url": "http://v12.cat.api.test.streamco.com.au/feeds/season-0098904009.json"
        }

### Retrieve a Single Season [GET]
+ Response 200

    [Season][]

## All Programs [/feeds/{cpToken}/programs?category={id}&programType={programType}]
Collection of all Programs.

The Program Collection resource has the following attribute:

- total

+ Parameters
    + id (string) ... Unique ID of the category of Program
    + cpToken (string) ... Unique token of the Content Provider 
    + programType (string) ... Type of program to fetch. Eg: 'movie', 'episode', 'tvshow' etc

+ Model (application/json)

    JSON representation of all programs feed

    + Body

     {
          "url": "http://url",
          "total": 3,
          "next": "http://nexturl",
          "prev": "http://prevurl",
          "entries": [
            {
              "url": "http://v12.cat.api.test.streamco.com.au/programs/0372784.json",
              "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103832614612",
              "guid": "0372784",
              "title": "Batman Begins (2005)",
              "shortTitle": "",
              "description": "Directed by Christopher Nolan.  With Christian Bale, Michael Caine, Ken Watanabe, Liam Neeson. After training with his mentor, Batman begins his war on crime to free the crime-ridden Gotham City from corruption that the Scarecrow and the League of Shadows have cast upon it.",
              "shortDescription": "Directed by Christopher Nolan.  With Christian Bale, Michael Caine, Ken Watanabe, Liam Neeson. After training with his mentor, Batman begins his war on crime to free the crime-ridden Gotham City from corruption that the Scarecrow and the League of Shadows have cast upon it.",
              "added": 1414531395000,
              "updated": 1414531396000,
              "programType": "movie",
              "images": {
                "Cast in Character": {
                  "url": "http://img.streamco.com.au/dev/tt0372784_landscape.jpg",
                  "width": 1920,
                  "height": 1080
                },
                "Poster Art": {
                  "url": "http://img.streamco.com.au/dev/tt0372784_poster.jpg",
                  "width": 630,
                  "height": 1023
                }
              },
              "pricingType": "PPV",
              "pricing": {
                "watchnow": {
                  "id": "1234s4",
                  "price": {
                    "INR": 50,
                    "USD": 0.99
                  }
                },
                "watchlater": {
                  "id": "453421",
                  "price": {
                    "INR": 20,
                    "USD": 0.4
                  }
                },
                "pack": {
                  "id": "451232",
                  "price": {
                    "INR": 100,
                    "USD": 2
                  }
                }
              },
              "classifications": [
                {
                  "scheme": "",
                  "rating": "M",
                  "consumerAdvice": [
                    "one use of moderate language",
                    "mild sex references"
                  ]
                }
              ],
              "credits": {
                "cast": [
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/christian-bale",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/christian-bale",
                    "personName": "Christian Bale"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-caine",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-caine",
                    "personName": "Michael Caine"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ken-watanabe",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ken-watanabe",
                    "personName": "Ken Watanabe"
                  }
                ],
                "directors": [
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/christopher-nolan",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/christopher-nolan",
                    "personName": "Christopher Nolan"
                  }
                ]
              },
              "tags": [
                {
                  "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Action",
                  "scheme": "genre",
                  "title": "Action"
                },
                {
                  "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Crime",
                  "scheme": "genre",
                  "title": "Crime"
                },
                {
                  "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Thriller",
                  "scheme": "genre",
                  "title": "Thriller"
                }
              ],
              "duration": 8400,
              "releaseYear": 2005,
              "languages": [
                "Urdu",
                "English",
                "Mandarin"
              ],
              "highDefinition": false,
              "closedCaptions": [
                "English"
              ],
              "audioLayout": "",
              "new": false,
              "exclusive": false,
              "countries": [
                "United States",
                "United Kingdom"
              ],
              "related": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Action%26byExcludeId=103832614612",
              "seriesPremiere": "2005-01-01",
              "seriesFinale": ""
            },
            {
              "url": "http://v12.cat.api.test.streamco.com.au/programs/1628033.json",
              "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103855142460",
              "guid": "1628033",
              "title": "Top Gear",
              "shortTitle": "",
              "description": "Jeremy, Richard and James talk about everything car-related. From new cars to how they're fueled, this show has it all.",
              "shortDescription": "",
              "added": 1414532875000,
              "updated": 1414532898000,
              "programType": "series",
              "images": {
                "Cast in Character": {
                  "url": "http://img.streamco.com.au/dev/tt1628033_landscape.jpg",
                  "width": 1920,
                  "height": 1080
                },
                "Poster Art": {
                  "url": "http://img.streamco.com.au/dev/tt1628033_poster.jpg",
                  "width": 630,
                  "height": 1023
                }
              },
              "classifications": null,
              "credits": {
                "cast": [
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-hammond",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-hammond",
                    "personName": "Richard Hammond"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jeremy-clarkson",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jeremy-clarkson",
                    "personName": "Jeremy Clarkson"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-may",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-may",
                    "personName": "James May"
                  }
                ],
                "directors": [
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/brian-klein",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/brian-klein",
                    "personName": "Brian Klein"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/phil-churchward",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/phil-churchward",
                    "personName": "Phil Churchward"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/nigel-simpkiss",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/nigel-simpkiss",
                    "personName": "Nigel Simpkiss"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-heeley",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-heeley",
                    "personName": "Richard Heeley"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-bryce",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-bryce",
                    "personName": "James Bryce"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/andrew-fettis",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/andrew-fettis",
                    "personName": "Andrew Fettis"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/owen-trevor",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/owen-trevor",
                    "personName": "Owen Trevor"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/konrad-begg",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/konrad-begg",
                    "personName": "Konrad Begg"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/scott-weintrob",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/scott-weintrob",
                    "personName": "Scott Weintrob"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-duncan",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-duncan",
                    "personName": "Ben Duncan"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/chris-richards",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/chris-richards",
                    "personName": "Chris Richards"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/kit-lynch-robinson",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/kit-lynch-robinson",
                    "personName": "Kit Lynch Robinson"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/southan-morris",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/southan-morris",
                    "personName": "Southan Morris"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-hume-paton",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-hume-paton",
                    "personName": "Ben Hume-Paton"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/edward-bazalgette",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/edward-bazalgette",
                    "personName": "Edward Bazalgette"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/chris-hooke",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/chris-hooke",
                    "personName": "Chris Hooke"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/simon-ludgate",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/simon-ludgate",
                    "personName": "Simon Ludgate"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-tuft",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-tuft",
                    "personName": "Michael Tuft"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lee-ford",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lee-ford",
                    "personName": "Lee Ford"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jon-richards",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jon-richards",
                    "personName": "Jon Richards"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ed-venner",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ed-venner",
                    "personName": "Ed Venner"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/iain-may",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/iain-may",
                    "personName": "Iain May"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/graham-sherrington",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/graham-sherrington",
                    "personName": "Graham Sherrington"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-southwell",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/ben-southwell",
                    "personName": "Ben Southwell"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/dennis-jarvis",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/dennis-jarvis",
                    "personName": "Dennis Jarvis"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-thomson",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-thomson",
                    "personName": "Richard Thomson"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/rob-kaplan",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/rob-kaplan",
                    "personName": "Rob Kaplan"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/gary-broadhurst",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/gary-broadhurst",
                    "personName": "Gary Broadhurst"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/babak-zand-goodarzi",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/babak-zand-goodarzi",
                    "personName": "Babak Zand Goodarzi"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-massey",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-massey",
                    "personName": "Michael Massey"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-armitt",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-armitt",
                    "personName": "Michael Armitt"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/gerry-pomeroy",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/gerry-pomeroy",
                    "personName": "Gerry Pomeroy"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-wiseman",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-wiseman",
                    "personName": "James Wiseman"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lionel-mill",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lionel-mill",
                    "personName": "Lionel Mill"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/hannah-springham",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/hannah-springham",
                    "personName": "Hannah Springham"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/benji-edwards",
                    "characterName": "",
                    "creditType": "director",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/benji-edwards",
                    "personName": "Benji Edwards"
                  }
                ]
              },
              "tags": [
                {
                  "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Comedy",
                  "scheme": "genre",
                  "title": "Comedy"
                }
              ],
              "pricingType": "Subscription",
              "pricing": {
                "pack": {
                  "id": "451232",
                  "price": {
                    "INR": 100,
                    "USD": 2
                  }
                }
              },
              "duration": 0,
              "releaseYear": 2002,
              "languages": [],
              "highDefinition": false,
              "closedCaptions": null,
              "audioLayout": "",
              "new": false,
              "exclusive": false,
              "countries": [
                "United Kingdom"
              ],
              "seasons": [
                {
                  "url": "http://v12.cat.api.test.streamco.com.au/feeds/season-1628033021.json",
                  "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/TvSeason/102344742786",
                  "guid": "1628033021",
                  "title": "21",
                  "seasonNumber": 21,
                  "releaseYear": 2014
                },
                {
                  "url": "http://v12.cat.api.test.streamco.com.au/feeds/season-1628033020.json",
                  "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/TvSeason/102344230877",
                  "guid": "1628033020",
                  "title": "season-21",
                  "seasonNumber": 20,
                  "releaseYear": 2013
                }
              ],
              "related": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byTags=genre:Comedy",
              "seriesPremiere": "2002-01-01",
              "seriesFinale": ""
            },
            {
              "url": "http://v12.cat.api.test.streamco.com.au/programs/0697695.json",
              "seriesUrl": "http://v12.cat.api.test.streamco.com.au/programs/0098904.json",
              "id": "http://data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103854630585",
              "guid": "0697695",
              "title": "The Finale",
              "shortTitle": "",
              "description": "After George and Jerry land a production deal with NBC, the four head out for Paris on NBC's private plane and are waylaid in a small Massachusetts town.",
              "shortDescription": "",
              "added": 1414532373000,
              "updated": 1414532373000,
              "programType": "episode",
              "images": {},
              "classifications": [
                {
                  "scheme": "",
                  "rating": "PG",
                  "consumerAdvice": []
                }
              ],
              "credits": {
                "cast": [
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jerry-seinfeld",
                    "personName": "Jerry Seinfeld"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/julia-louis-dreyfus",
                    "personName": "Julia Louis-Dreyfus"
                  },
                  {
                    "url": "http://streamco-search-test.elasticbeanstalk.com/search?q=custom&customQuery=%26byPersonId=http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
                    "characterName": "",
                    "creditType": "cast",
                    "personId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/michael-richards",
                    "personName": "Michael Richards"
                  }
                ]
              },
              "tags": null,
              "runtime": 0,
              "releaseYear": 1998,
              "languages": [],
              "highDefinition": false,
              "closedCaptions": null,
              "audioLayout": "",
              "new": false,
              "exclusive": false,
              "countries": null,
              "seriesId": "http://data.entertainment.tv.theplatform.com/entertainment/data/Program/guid/2463874319/0098904",
              "tvSeasonEpisodeNumber": 22,
              "tvSeasonNumber": 9,
              "expirationDate": 1446067200000,
              "seriesPremiere": "1998-01-01",
              "seriesFinale": ""
            }
          ]
        }



### List All Programs [GET]

+ Response 200

    [All Programs][]
    
## Search [/feeds/search/{query}]
Response from Unified Search Request.

The Program Collection resource has the following attribute:

- total

+ Parameters
    + query (string) ... Search query

+ Model (application/json)

    JSON representation of all programs feed

    + Body
    {
    "url": "http: //url",
    "total": 3,
    "next": "http: //nexturl",
    "prev": "http: //prevurl",
    "entries": [
        {
            "url": "http: //v12.cat.api.test.streamco.com.au/programs/1628033.json",
            "id": "http: //data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103855142460",
            "guid": "1628033",
            "title": "TopGear",
            "shortTitle": "",
            "description": "Jeremy,RichardandJamestalkabouteverythingcar-related.Fromnewcarstohowthey'refueled,thisshowhasitall.",
            "shortDescription": "",
            "added": 1414532875000,
            "updated": 1415326504000,
            "programType": "series",
            "images": {
                "CastinCharacter": {
                    "url": "http: //img.streamco.com.au/dev/tt1628033_landscape.jpg",
                    "width": 1920,
                    "height": 1080
                },
                "PosterArt": {
                    "url": "http: //img.streamco.com.au/dev/tt1628033_poster.jpg",
                    "width": 630,
                    "height": 1023
                }
            },
            "pricing": {
                "watchnow": {
                  "id": "1234s4",
                  "price": {
                    "INR": 50,
                    "USD": 0.99
                  }
                },
                "watchlater": {
                  "id": "453421",
                  "price": {
                    "INR": 20,
                    "USD": 0.4
                  }
                },
                "pack": {
                  "id": "451232",
                  "price": {
                    "INR": 100,
                    "USD": 2
                  }
                }
              },
            "classifications": null,
            "credits": {
                "cast": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-hammond",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-hammond",
                        "personName": "RichardHammond"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jeremy-clarkson",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/jeremy-clarkson",
                        "personName": "JeremyClarkson"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-may",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-may",
                        "personName": "JamesMay"
                    }
                ],
                "directors": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/brian-klein",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/brian-klein",
                        "personName": "BrianKlein"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/phil-churchward",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/phil-churchward",
                        "personName": "PhilChurchward"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/nigel-simpkiss",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/nigel-simpkiss",
                        "personName": "NigelSimpkiss"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-heeley",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/richard-heeley",
                        "personName": "RichardHeeley"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-bryce",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/james-bryce",
                        "personName": "JamesBryce"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/andrew-fettis",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/andrew-fettis",
                        "personName": "AndrewFettis"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/owen-trevor",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/owen-trevor",
                        "personName": "OwenTrevor"
                    }
                ],
                "producers": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "personName": "SidneyLumet"
                    }
                ]
            },
            "tags": [
                {
                    "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byTags=genre: Comedy",
                    "scheme": "genre",
                    "title": "Comedy"
                }
            ],
            "runtime": 0,
            "releaseYear": 2002,
            "languages": [],
            "highDefinition": false,
            "closedCaptions": null,
            "audioLayout": "",
            "new": false,
            "exclusive": true,
            "countries": [
                "UnitedKingdom"
            ],
            "related": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byTags=genre: Comedy%26byExcludeId=103855142460",
            "seriesPremiere": "2002-01-01",
            "seriesFinale": ""
        },
        {
            "url": "http: //v12.cat.api.test.streamco.com.au/programs/0050083.json",
            "id": "http: //data.entertainment.tv.theplatform.com/entertainment/data/ProgramAvailability/103832614516",
            "guid": "0050083",
            "title": "12AngryMen(1957)",
            "shortTitle": "",
            "description": "DirectedbySidneyLumet.WithHenryFonda,LeeJ.Cobb,MartinBalsam,JohnFiedler.Adissentingjurorinamurdertrialslowlymanagestoconvincetheothersthatthecaseisnotasobviouslyclearasitseemedincourt.",
            "shortDescription": "",
            "added": 1414531231000,
            "updated": 1414531232000,
            "programType": "movie",
            "pricing": {
                "watchnow": {
                  "id": "1234s4",
                  "price": {
                    "INR": 50,
                    "USD": 0.99
                  }
                },
                "watchlater": {
                  "id": "453421",
                  "price": {
                    "INR": 20,
                    "USD": 0.4
                  }
                },
                "pack": {
                  "id": "451232",
                  "price": {
                    "INR": 100,
                    "USD": 2
                  }
                }
              },
            "images": {
                "CastinCharacter": {
                    "url": "http: //img.streamco.com.au/dev/tt0050083_landscape.png",
                    "width": 1920,
                    "height": 1080
                },
                "PosterArt": {
                    "url": "http: //img.streamco.com.au/dev/tt0050083_poster.jpg",
                    "width": 630,
                    "height": 1023
                }
            },
            "classifications": [
                {
                    "scheme": "",
                    "rating": "G",
                    "consumerAdvice": [
                        "descriptionsofviolence"
                    ]
                }
            ],
            "credits": {
                "cast": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/henry-fonda",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/henry-fonda",
                        "personName": "HenryFonda"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lee-j.-cobb",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/lee-j.-cobb",
                        "personName": "LeeJ.Cobb"
                    },
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/martin-balsam",
                        "characterName": "",
                        "creditType": "cast",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/martin-balsam",
                        "personName": "MartinBalsam"
                    }
                ],
                "directors": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "personName": "SidneyLumet"
                    }
                ],
                "producers": [
                    {
                        "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byPersonId=http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "characterName": "",
                        "creditType": "director",
                        "personId": "http: //data.entertainment.tv.theplatform.com/entertainment/data/Person/guid/2463874319/sidney-lumet",
                        "personName": "SidneyLumet"
                    }
                ]
            },
            "tags": [
                {
                    "url": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byTags=genre: Drama",
                    "scheme": "genre",
                    "title": "Drama"
                }
            ],
            "runtime": 5760,
            "releaseYear": 1957,
            "languages": [
                "English"
            ],
            "highDefinition": false,
            "closedCaptions": [
                "English"
            ],
            "audioLayout": "",
            "new": false,
            "exclusive": false,
            "countries": [
                "UnitedStates"
            ],
            "related": "http: //streamco-search-test.elasticbeanstalk.com/search?q=byTags=genre: Drama%26byExcludeId=103832614516",
            "seriesPremiere": "1957-01-01",
            "seriesFinale": ""
        }
    ]
}

### Unified Search [GET]

+ Response 200

    [Search][]


## Register User [/account]

+ Body
    {
        "msisdn": "919995434565",
        "otp": "4722"
    }

### User Registration.[POST]
+ Response 200 (application/json)

        {"msisdn": "+919910001111", "token": "mUUUqQlbFSk=", "uid": "T5keGtPpUFnLAAAA","dupd":false,"circleLang":["en","hi"]}

## Request OTP [/account/otp]

+ Body
    {
        "msisdn":"919991000111"
    }

### OTP Request [POST]
+ Response 200 (application/json)

        {"msisdn" : "+919910001111"}


## User profile APIs [/account/profile?uid={uid}&token={token}]

+ Parameters
    + uid (string) ... Unique ID of the App User
    + token (string) ... Token received while Registering user in App

### View Profile [GET]
+ Response 200 (application/json)

        {"uid":"TpMsCP-Yr9uPVWl_u0","contentLang":["en","hi","pa"],"email":"test@test.in","dob":{"month":2,"year":1989,"day":2},"name":"Test","gender":"m","avatar":"http:\/\/s3-ap-southeast-1.amazonaws.com\/test.png","songQuality":"h","lang":"en"}


### Update Profile [POST]
+ Body
    {
        "name":"test",
        "gender":"m"
    }
+ Response 200 (application/json)

        {
        "uid":"TpMsCP-Yr9uPVWl_u0",
        "status":true,
        "name":"test",
        "gender":"m"
        }  
