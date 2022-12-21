package calendar.utilities;

class EmailBuilder {

    static String buildEmail(String title, String event, String approveLink, String rejectLink) {
        return '\n' +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                '\n' +
                "<head>\n" +
                '\n' +
                "<meta charset=\"utf-8\">\n" +
                "<meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                "<title>Invitation</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<style type=\"text/css\">\n" +
                "/**\n" +
                "   * Google webfonts. Recommended to include the .woff version for cross-client compatibility.\n" +
                "   */\n" +
                "        @media screen {\n" +
                "            @font-face {\n" +
                "                font-family: 'Source Sans Pro';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 400;\n" +
                "                src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n" +
                "            }\n" +
                '\n' +
                " @font-face {\n" +
                "                font-family: 'Source Sans Pro';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 700;\n" +
                "                src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n" +
                "            }\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Avoid browser level font resizing.\n" +
                "   * 1. Windows Mobile\n" +
                "   * 2. iOS / OSX\n" +
                "   */\n" +
                "        body,\n" +
                "        table,\n" +
                "        td,\n" +
                "        a {\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "            /* 1 */\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "            /* 2 */\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Remove extra space added to tables and cells in Outlook.\n" +
                "   */\n" +
                "        table,\n" +
                "        td {\n" +
                "            mso-table-rspace: 0pt;\n" +
                "            mso-table-lspace: 0pt;\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Better fluid images in Internet Explorer.\n" +
                "   */\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Remove blue links for iOS devices.\n" +
                "   */\n" +
                "        a[x-apple-data-detectors] {\n" +
                "            font-family: inherit !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Fix centering issues in Android 4.4.\n" +
                "   */\n" +
                "        div[style*=\"margin: 16px 0;\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                '\n' +
                "        body {\n" +
                "            width: 100% !important;\n" +
                "            height: 100% !important;\n" +
                "            padding: 0 !important;\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                '\n' +
                "        /**\n" +
                "   * Collapse table borders to avoid space between cells.\n" +
                "   */\n" +
                "        table {\n" +
                "            border-collapse: collapse !important;\n" +
                "        }\n" +
                '\n' +
                "        a {\n" +
                "            color: #1a82e2;\n" +
                "        }\n" +
                '\n' +
                "       img {\n" +
                "            height: auto;\n" +
                "            line-height: 100%;\n" +
                "            text-decoration: none;\n" +
                "            border: 0;\n" +
                "            outline: none;\n" +
                "        }\n" +
                "    </style>\n" +
                '\n' +
                "</head>\n" +
                '\n' +
                "<body style=\"background-color: #e9ecef;\">\n" +
                '\n' +
                "    <!-- start preheader -->\n" +
                "    <div class=\"preheader\"\n" +
                "        style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n" +
                "        A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n" +
                "    </div>\n" +
                "    <!-- end preheader -->\n" +
                '\n' +
                "    <!-- start body -->\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                '\n' +
                "        <!-- start logo -->\n" +
                "        <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" +
                "                            <a href=\"app link here\" target=\"_blank\" style=\"display: inline-block;\">" + '\n'
                + "<img src="+"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9uf1--mfbG2VGXk2uVmOnEpWFC2b4T-QfUJcrahDDCOAcsYgBDAq_fTtaTW4qWBTA0vY&usqp=CAU" + '\n' +
                "                                    alt=\"Logo\" border=\"0\" width=\"50\"\n" +
                "                                    style=\"display: block; width: 50px; max-width: 50px; min-width: 50px;\">\n" +
                "                            </a>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <!-- end logo -->\n" +
                '\n' +
                "        <!-- start hero -->\n" +
                "        <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" bgcolor=\"#ffffff\"\n" +
                "                            style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
                "                            <h1\n" +
                "                                style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">\n" +
                "                                Event invitation</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <!-- end hero -->\n" +
                '\n' +
                "        <!-- start copy block -->\n" +
                "        <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                '\n' +
                "                    <!-- start copy -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" bgcolor=\"#ffffff\"\n" +
                "                            style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "                            <p style=\"margin: 0;\">You are invited to " + title + "\n" + event + "\n" +
                                                "\nTap the approve / reject button below to approve / reject the invitation. \n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end copy -->\n" +
                '\n' +
                "                    <!-- start button -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" bgcolor=\"#ffffff\">\n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" +
                "                                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" +
                "                                                    <a href=" + approveLink + " target=\"_blank\"\n" +
                "                                                        style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Do\n" +
                "                                                        Approve</a>\n" +
                "                                                </td>\n" +
                "                                                <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" +
                "                                                    <a href=" + rejectLink + " target=\"_blank\"\n" +
                "                                                        style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Do\n" +
                "                                                        Reject</a>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end button -->\n" +
                '\n' +
                "                    <!-- start copy -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" bgcolor=\"#ffffff\"\n" +
                "                            style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "                            <p style=\"margin: 0;\">If that doesn't work, and you want to approve, copy and paste the following link in your\n" +
                "                                browser:</p>\n" +
                "                            <p style=\"margin: 0;\"><a href="+approveLink+'\n'+ " target=\"_blank\">approve</a></p>\n" +
                "                            <p style=\"margin: 0;\">If that doesn't work, and you want to reject, copy and paste the following link in your\n" +
                "                                browser:</p>\n" +
                "                            <p style=\"margin: 0;\"><a href="+rejectLink+'\n'+ " target=\"_blank\">reject</a></p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end copy -->\n" +
                '\n' +
                "                    <!-- start copy -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" bgcolor=\"#ffffff\"\n" +
                "                            style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\n" +
                "                            <p style=\"margin: 0;\">Cheers,<br> Calendar App</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end copy -->\n" +
                '\n' +
                "                </table>\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <!-- end copy block -->\n" +
                '\n' +
                "        <!-- start footer -->\n" +
                "        <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                '\n' +
                "                    <!-- start permission -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" bgcolor=\"#e9ecef\"\n" +
                "                            style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n" +
                "                            <p style=\"margin: 0;\">You received this email because we received a request for\n" +
                "                                [type_of_action] for your account. If you didn't request [type_of_action] you can safely\n" +
                "                                delete this email.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end permission -->\n" +
                '\n' +
                "                    <!-- start unsubscribe -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" bgcolor=\"#e9ecef\"\n" +
                "                            style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n" +
                "                            <p style=\"margin: 0;\">To stop receiving these emails, you can unsubscribe at any time.</p>\n" +
                "     <p style=\"margin: 0;\">Calendar App at 1234 S. Broadway Eden. City, Omar State 12345</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <!-- end unsubscribe -->\n" +
                '\n' +
                "                </table>\n" +
                "                <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "<![endif]-->\n" +
                "      </td>\n" +
                "        </tr>\n" +
                "        <!-- end footer -->\n" +
                '\n' +
                "</table>\n" +
                "<!-- end body -->\n" +
                '\n' +
                "</body>\n" +
                '\n' +
                "</html>\"";
    }
}
