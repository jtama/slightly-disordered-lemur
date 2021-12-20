package com.onepoint.yap.ri;

public enum Invasion {
    SKULL("""
            …………………………………………………………………_„„„„„„——~~~——„„„„„„_
            ……………………………………………………..„„-~^*'''¯¯::::::::::::::::::::::::::::::::::::¯''''*^^~-„„„_
            …………………………………………….„„-~^*''¯:::::::::::::_„„„„„„„————-„„„„„_::::::::::::::::::::::¯''*^~-„„
            ………………………………………„„-^*'':::::::::::„„-~^*'''¯¯::::::::::::::::::::::::::::::¯¯''''*^^^~-„„:::::::::::::::::¯'*^-„_
            ………………………………….„-^''::::::::::„„-^*''¯:::::::::::::::::::„„-^*''¯¯¯¯'''*^~-„„::::::::::::::::::¯'''*^~-„„::::::::::::::''^-„
            ……………………………...„-^''::::::::„„-^*''::::::::::::::::::::::::::/:::::::::::::::::::::::¯''^-„:::::::::::::::::::::::¯''*^-„:::::::::::''^-„
            ………………………….„-^'':::::::„„-^'':::::::::::::::::::::::::::::::::'\\:::::::::::::::::::_„„„„_:::''-„::::::::::::::::::::::::::::''*^-„:::::::::''-„
            ……………………....„-''::::::::„-'':::::::::::::„„-~~^*'''¯¯„''-:::::::::::''^-„„„___„„-^*''::::::::¯''¯::::::::::::„„-^'''''*^~-„„::::::::''^-„::::::::''-„
            ……………………„-''::::::::„-''::::::::::::„-^''::::::::::::„-''::::::::::::::::::::::_____::::::::::::::::::::::::::/:::::::::::::'''^-„::::::::''^-„:::::::''-„
            …………………„-'':::::::„-''':::::::::::„-'':::::::::::::::/''::::::_„„-~^*''''''''''¯¯¯ : : : : :¯¯¯¯'''''''*^~-„„_:::::'\\::::::::::::::::::''\\::::::::::''-„:::::::''-„
            ………………„-'':::::::„-''::::::::::::::'|:::::::::::::::/'::::„-^'' : : : : : : : : : : : : : : : : : : : : : : : : :''^-„:::''^-„„:::::::::::::::\\:::::::::::''-„:::::::'\\
            …………….„-''::::::::/::::::::::::::::::'\\:::::::::„„-''::„-''' : : : : : : : : : : : : : : : : : : : : : : : : : : : : : '''-„:::::¯''*^~-„„::::::'|:::::::::::::''-„::::::'\\
            ………….../:::::::::/'::::::::::::::::::::::''*^~^'':::::/ : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : :'\\::::::::::::::¯''-„::'|:::::::::::::::'\\::::::''\\
            …………./:::::::::/::::::::::::::::::::::::::::::::::::'| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : \\:::::::::::::::::''-''::::::::::::::::::\\::::::'\\
            …………/::::::::/':::::::::„-^'''/::::::::::::::::::::::'| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : \\::::::::::::::::::::::::::::::::::::::'\\::::::'\\
            ………../::::::::/:::::::„-''::::'|:::::::::::::::::::::::'| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : \\:::::::::„-~^^-„:::::::::::::::::::::\\::::::'\\
            ………/::::::::/:::::::/':::::::'|::::::::::::::::::::::::| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : :|:::::::/:::::::::''-„:::::::::::::::::::'\\::::::'\\
            ……../::::::::/:::::::/:::::::::'|::::::::::::::::::::::::| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : :|::::::|:::::::::::::\\::::::::::::::::::::\\::::::'\\
            ……./::::::::/:::::::/:::::::::::'\\::::::::::„-''|:::::::::| : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : '|:::::::\\:::::::::::::'\\::::::::::::::::::::\\::::::'\\
            ……'|:::::::'|:::::::/:::::::::::::'/:::::::/'':::'|:::::::::'| : : : :_„„„„_ : : : : : : : : : : : : : : : : : : : : : :_ : : : : : '|::::::::''-„::::::::::::|::::::::::::::::::::\\::::::'\\
            ……|:::::::'|:::::::'\\::::::::::„-''::::::/'::::::'|:::::::::'| : : /¯ . . . .''^-„ : : : : „- : : :^-„ : : : : : :„-^''¯ . ¯''^-„ : : |::::::::::::''-„::::::::/:::::::::::::::::::::'|::::::'|
            ……|:::::::|:::::::::''*^~~^''::::::::/:::::::::''-„:::::::| : :| . . . . . . . ''^-„„_ : : : : : : : :„-~~^'' . . . . . . . '| : :|:::::::::::::::\\::::::/:::::::::::::::::::::::|::::::'|
            …...|:::::::|::::::::::::::::::::::::::/:::::::::::::\\::::::'| : :| . . . . . . . . . . .') : : : : : : (' . . . . . . . . . . . / : /:::::::::::::::::|:::/':::::::„-^*'''*^-„:::::::'|::::::'|
            ……|:::::::|:::::::::::::::::::::::::'|::::::::::::::|::::::/ : :'\\ . . . . . . . . „-^'' : :„-~-„ : : ''^-„ . . . . . . . . ./ : :\\::::::::::::::::::^''::::::::/:::::::::::'\\::::::|::::::'|
            ……|:::::::'|:::::::::::::::::::::::::|:::::::::::::/:::::/ : : : ''^-„„„__„„-^'' : : : :/' : : : ''-„ : : : ''*^-„„___„-^'' : : : :\\„„:::::::::::::::::::::::'|:::::::::::::'|:::::|::::::'|
            ……|:::::::'|::::::::::::::„::::::::::''^-„:::::„-''::::„-'' : : : : : : : : : : : : : : :/ : : : : : :'\\ : : : : : : : : : : : : : : : :'|:::::::::::::::::::::::\\:::::::::::::|::::'|::::::'|
            ……'|:::::::'|::::::::::::/::\\:::::::::::::¯¯:::::::::'\\ : : : : : : : : : : : : : : :'| : : : : : : :| : : : : : : : : : : : : : : :/'::::::„„-~-„::::::::::::'\\::::::::::/:::::|::::::'|
            …….'|:::::::'|::::::::::/::::''-„::::::::::::::::::::::::''-„ : : : : : : : : : : : : : :| : : : : : : | : : : : : : : : : : : : : „-''::::„-''::::::::''-„:::::::::::|:::::::/::::::|::::::'|
            ……..|:::::::'|:::::::::|::::::::''^-„:::::::::::::::::::::::''*^~-~^''-„ : : : : : : : ''~-^*'''*^~'' : : : : : : : „-^*'''''''|'''¯::::::/::::::::::::::'|::::::::::|::::„-''::::::'|::::::'|
            ……...|:::::::'\\::::::::|::::::::::::''-„:::::::::::::::::::::::::'| : :| :¯''-„ : : : : : : : : : : : : : : : : : :/' : | : : |:::::::::|::::::::::::::/::::::::::/„-^'':::::::::|::::::'|
            ……….\\::::::::\\::::::'\\::::::::::::::'\\::::::::::::::::::::::::'\\ : :| : :/_ : : : : : : : : : : : : : : : : :'| : :'| : : |:::::::::'|::::::::::::/:::::::::::::::::::::::::/::::::/
            ………...\\:::::::'\\:::::::\\:::::::::::::'|:::::::::::::::::::::::::| : :\\ : ( :|''^~-„-~-„--„„ „„„ „„„„ „--„-^*''\\ : / : :'/:::::::::/:::::::::„-'':::::::::::::::::::::::::/::::::'/
            ………….'\\:::::::'\\:::::::''-„::::::„-''::::::::|''\\:::::::::::::::'| : : :\\„/''*^-| : | : | : : | : |' : | :| :\\„-''-''|„/ : : :|::::::::/::::::„„-''::::::::::::::::::::::::::/'::::::/'
            …………...\\:::::::''-„:::::::¯''''¯:::::::::::'|:::''-„::::::::::::| : : : : ''-„/ ''-„''*^~~~„~„-~„^^'\\''¯\\ '' : : : : :|:::::„-''_„„-^''::::::::::::::::::::::::::::/'::::::/'
            …………….'\\::::::::''-„:::::::::::::::::::::'|::::::''*-„„:::::::'\\ : : : : : : :''-„„|__|_|_'|_„„\\„-''' : '' : : : : :/':::::::¯:::„„-~^^-„:::::::::::::::::::::/'::::::/'
            ………………'\\:::::::::'\\::::::::::::::::::::\\:::::::::::''*-„:::::\\ : : : : : : : : : : : : : : : : : : : : : : : /::::::::::„-'':::::::::::''\\:::::::::::::::„-''::::„-''
            ………………..''-„::::::::''-„:::::::::::::::::'\\::::::::::::::''-„:::'^-„ : : : : : : : : : : : : : : : : : : : „-'':::::::::„-'':::::::::::::::'|:::::::::::„-'':::::„-''
            …………………..''-„::::::::''-„::::::::::::::::'\\::::::::::::::'|::::::''-„ : : : : : : : : : : : : : : : : „-'':::::::::„-''::::::::::::::::„-''::::::::„-'':::::„-''
            ……………………..''-„::::::::''^-„:::::::::::::''-„::::::::„-''::::::::::''^-„ : : : : : : : : : : : : „„-''::::::::„-''::::::::::::::„„-^''::::::::„-'':::::„-''
            ………………………..''^-„::::::::''^-„::::::::::::¯'''''''¯:::::::::::''-„¯''*^''*^~~^^*''*^^~~^*''\\:::::::::''~-„„„„„-~~^*'':::::::::„„-''::::::„-''
            ……………………………'''*-„„:::::::''*^-„:::::::::::::::::::::::::::''^-„:::::::::::::::::::::::::'|:::::::::::::::::::::::::::::„„-^''::::::„-'''
            ………………………………...''^-„::::::::¯''*^-„„_:::::::::::::::::::::¯''*^-„„__::::::::_„-^'':::::::::::::::::::::::„„-^'':::::::„„-''
            …………………………………….''*^-„„:::::::::::¯''*^~-„„_::::::::::::::::::::¯''''''''''¯:::::::::::::::_„„„-~^*''¯:::::::„„-^'''
            …………………………………………..''*^-„„_::::::::::::::¯''*^~-„„„„„__:::::::::::::__„„„-~^*''::::::::::::„„„-^''''
            ………………………………………………….¯''*^-„„„_:::::::::::::::::::¯''''''***''''''¯:::::::::::_„„„„--~^*'''
            ………………………………………DisorderedLemure........¯'''''''**^^~-„„„„„„____„„„„„„„-~~^''''¯
                        
            """),
    BENDER("""
            ………………………………………………...„--„
            ……………………………………………….('::::'')
            ………………………………………………...'|''|'
            …………………………………………………|::|
            ………………………………………………...|:::|
            ………………………………………………...|:::|
            ………………………………………………...|:::'|„_
            ……………………………………………..„-*¯**¯:::*-„„„_
            ………………………………………..„„-^*¯*^~~~^*¯ : : :¯*-„
            …………………………………….„-^* : : : : : : : : : : : : : : : *-„
            ……………………………………/ : : : : : : : : : : : : : : : : : : : \\
            …………………………………../ : : : : : : : : : : : : : : : : : : : : '|
            ………………………………….| : : : : : : : : : : : : : : : : : : : : : |
            ………………………………….| : : : : : : : : : : : : : : : : : : : : : '|
            ………………………………….| : : : : : : :„„-~--------„„„„„„„„---~^**¯*^-„„
            ………………………………….| : : : : :„-* : : : : : „„„„____„„„„„„„„„„„„„„„„¯*-„„
            ………………………………….| : : : : / : : : :„-^*¯**^~-„„„;;;;;;„-^*¯ *^„;¯*-„*,
            ………………………………….| : : : : | : : : / . .__. . . . .¯*„* __ . . . \\;;;;'\\:'\\
            ………………………………….| : : : : | : : : | . .|||||. . . . . .'| . ||||| . . .,/;;;;;| '|
            ………………………………….| : : : : :*-„ : : *~-„„„„„„„„„„„„„/'___„„„„„„-*_„„„-*„/'
            ………………………………….| : : : : : : *^~~------------------------„-----~^^*¯
            ………………………………….'| : : : : : : : : : : : : : : : : : : : : : |
            …………………………………..| : : : : :„„--,^^****,***^^,~~~,~~*'
            …………………………………..| : : : :/**^~|-----„„|„„„„„„-|„„„„„-|
            …………………………………..| : : : '\\„_ . | . . . '| . . . '| . . .|
            …………………………………..'| : : : :*-„¯'|*^^^^*|******|**''¯'|*-„
            ………………………………...„-*-„ : : : : :¯¯***********''''¯¯¯ : :|-„
            ……………………………„-^* : : : ¯**^^~-„„„___ : : :__„„„„-~^^* : :*~„
            ………………………..„-* : : : : : : : : : : : : : : :¯¯¯¯ : : : : : : : : : : : *^-„
            …………………….„-* : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : : \\
            …………………….|*^~-„„ : : : : : : : : : : : : : : : : : : : : : : : : : : : : _„-^*' '|-„
            …………………„-^*¯**^~„¯**^^~---„„„„___ : : : : : : : ___„„„„„--~^^*¯ : : : : | :''\\
            ……………...„-* : : : : : : :*-„ : : : : : : : : ¯¯¯''''''''''¯¯¯ : : : : : : : : : : : : : :| : :'\\„
            ……………../ „„-~^**-„ : : : :'\\ : : : : : : : : : : : : : : : : : : : : : : : _„„„-„ : : :| : : | ''*-„
            ……………„-* ; ; ; ; ; ;\\ : : : :| : : „-~^~~~-------„„„„„„„„„„-~^^***¯ : : : : | : :'| : : / ; „-*-„
            …………„-* ; ; ; ; ; ; „-* : : : / : : :| : : : : : : : : : : : : : : : : : : : : : : : | : :'| : „/„-^* ; ; \\
            ………../¯¯*^-„„ ; ;„-* : : „„-* : : : :'| : : : : : : : : : : : : : : : : : : : : : : : | : :'|-*-„ ; ; ; ; ; '\\
            ……..,/ ; ; ; ; ; *-/ ¯***¯ : : : : : : :| : : : : : : : : : : : : : : : : : : : : : : :'| : :'|….*-„ ; ;„„-* '\\
            ……,/ ; ; ; ; ; ;,/'…….| : : : : : : : :'| : : : : : : : : : : : : : : : : : : : : : : '| : :'|…….\\¯ ; ; ; ;'\\
            …../***^~-„„ ;,/……….'| : : : : : : : :| : : : : : : : : : : : : : : : : : : : : : : | : :'|……..\\ ; ; ; ; ;'\\
            …,/ ; ; ; ; ;¯'/………….| : : : : : : : '| : : : : : : : : : : : : : : : : : :„^**^„ '| : :'|………\\„„„-~^*¯'\\
            ..,/ ; ; ; ; ; ;/……………| : : : : : : : '| : : : : : : : : : : : : : : : : : :*-„-* :| : :'|……….\\ ; ; ; ; ; \\
            ..|_„„„---„„_'/…………….'| : : : : : : : '| : : : : : : : : : : : : : : : : : : : : : | : : |………..| ; ; ; ; ;'|
            ..| ; ; ; ; ; '|………………| : : : : : : : | : : : : : : : : : : : : : : : : : : : : :'| : : |………..|~-„„--^*''|
            ..| ; ; ; ; ; '|………………'| : : : : : : : | : : : : : : : : : : : : : : : : : : : : :| : :'|………..'| ; ; ; ; ;'|
            ..'\\ ; ; ; _„„„\\………………| : : : : : : : | : : : : : : : : : : : : : : : : : : : :'| : : |………...| ; ; ; ; ;'|
            …'\\„-^*¯ ; ; '\\……………..'| : : : : : : : | : : : : : : : : : : : : : : : : : : : :| : :'|…………|^~~~^*''|
            ….'\\ ; ; ; ; ; ;'\\…………….'| : : : : : : : | : : : : : : : : : : : : : : : : : : „-* : :|…………| ; ; ; ; ; |
            …...'\\ ; ; ; ;„„-^*-„…………..| : : : : : : :*~-„„_ : : : : : : : : __„„„--^*'' : : : |…………'| ; ; ; ;_„|
            ……..*-„„-* ; ; ; ;'*-„………..'| : : : : : : : : : : ¯¯******¯¯¯: : : : : : : :_„-*…………/¯*****¯ : \\
            ……….*-„ ; ; ; ; „-^**^--„……'*~-„„_ : : : : : : : : : : : : : : : : : __„„-^*„………….../: : : : : : : :\\
            ………….*-„ „-* : : : : : : ¯*^-„…..\\ ¯*****^^~~„~----~~^^-„**¯ ; ; ; ; ;*-„………../„_ : : : : : : : :\\
            …………….''\\ : : : : : : : : „-^**\\….*-„ ; ; ; ; ; ; \\………….*-„ ; ; ; ; ; ; „-'-„……./ : ¯,*^^^^^^,**¯\\
            ……………….\\ : : : : „-^*' : \\ : :\\…...\\___„„„-^^*'\\…………..'*-„ ; _„-^* ; ; \\…./„_„,-*./ : : /..'| : : |
            ………………...\\_„-^* : \\ : : :\\„„-*……\\ ; ; ; ; ; ; ;'\\…………….\\¯ ; ; ; ; ; ; \\……….*~--*…,***¯
            …………………./ \\ : : :'|_„„-*…………\\ ; ; ; ; ; ; ;\\…………….*-„ ; ; ; ; ; „-'„
            ………………….¯*~~*'………………..\\ ; ; ; ; ; ; „\\………………\\ ; _„„-^* ; '\\
            …………………………………………..|^^^^^^**¯ ; |……………….\\ ; ; ; ; ; ; '|
            …………………………………………..| ; ; ; ; ; ; ;'|………………..| ; ; ; ; ; „„|
            …………………………………………./ ; ; ; ; ; ; ; |………………..'|^^^^^^**¯ '|
            …………………………………………/*^~--„„„„„-^*'/………………../ ; ; ; ; ; ; ;|
            ………………………………………../ ; ; ; ; ; ; ; /…………………/ ; ; ; ; ; ; '/
            ………………………………………/ ; ; ; ; ; ; ; ;/…………………/*^~------^*/
            ……………………………………../¯*^~--„„„„„--/…………………./ ; ; ; ; ; ; /
            ………………………………….„-* ; ; ; ; ; ; ; /…………………../ ; ; ; ; ; ; /
            ………………………………..„-*-„ ; ; ; ; ; ; /…………………../ ; ¯*^^^^^*'/
            …………………………….„-* ; ; ;¯*^~-„„„'/………………..„-^*/„ ; ; ; ; ;_„/^-„„_
            ……………………_„„-*„ ; ; ; ; ; ; ; ;„-*…………….„-^* : : :¯**^^^^^* : : : : :*-„
            …………………..„-^* : : : *~--„„„„„„---^*-„„………….„-* : : : : : : : : : : : : : : : : : :*-„
            ……………….„-* : : : : : : : : : : : : : : : : *^-„......./ : : : : : : : : : : : : : : : : : : : : : \\
            …………….../ : : : : : : : : : : : : : : : : : : : : *-„.../_ : : : : : : : :: : : : : : : : : : : :_„-*
            ……………./ : : : : : : : : : : : : : : : : : : : : : : : \\…¯¯**^~~~---„„„„„„„„„„„---^^^***¯
            ……………..¯*^~---„„_ : : : : : : : : : : _„„„„„----^*'
            ………………………..¯¯¯¯*********¯
            Disordered Lemure
            """);

    private String message;

    Invasion(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return message;
    }
}
