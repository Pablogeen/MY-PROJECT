package com.rey.me.helper;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailBuilder {

    public String buildEmail(String firstname, String token) {
        String html = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Verify Your Email</title>
    </head>
    <body style="margin:0;padding:0;background-color:#f6f6f6;font-family:Helvetica,Arial,sans-serif;">
        <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f6f6f6;padding:20px 0;">
            <tr>
                <td align="center">
                    <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                        <!-- Header -->
                        <tr>
                            <td style="background-color:#1a73e8;padding:30px;text-align:center;border-radius:8px 8px 0 0;">
                                <h1 style="margin:0;color:#ffffff;font-size:28px;font-weight:bold;">BEN & CO</h1>
                            </td>
                        </tr>
                        <!-- Body Content -->
                        <tr>
                            <td style="padding:40px 30px;">
                                <h2 style="margin:0 0 20px 0;color:#0b0c0c;font-size:24px;">Hi {{firstname}},</h2>
                                <p style="margin:0 0 20px 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                    Thank you for registering with BEN & CO. Complete your registration by verifying your email address. Copy and paste the token to get your account verified:
                                </p>
                                <!-- Token Display -->
                                <table width="100%" cellpadding="0" cellspacing="0" style="margin:30px 0;">
                                    <tr>
                                        <td align="center">
                                            <div style="display:inline-block;background-color:#1a73e8;color:#ffffff;padding:15px 40px;border-radius:5px;font-size:18px;font-weight:bold;letter-spacing:2px;">{{token}}</div>
                                        </td>
                                    </tr>
                                </table>

                                <p style="margin:20px 0 0 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                    This token will expire in 15 minutes for security reasons.
                                </p>

                                <p style="margin:20px 0 0 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                    If you didn't create an account with us, you can safely ignore this email.
                                </p>
                            </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                            <td style="background-color:#f8f9fa;padding:20px 30px;text-align:center;border-radius:0 0 8px 8px;">
                                <p style="margin:0;color:#6c757d;font-size:14px;">
                                    © 2026 BEN & CO. All rights reserved.
                                </p>
                                <p style="margin:10px 0 0 0;color:#6c757d;font-size:12px;">
                                    This is an automated email. Please do not reply to this message.
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </body>
    </html>
    """;

        return html.replace("{{firstname}}", firstname == null ? "" : HtmlUtils.htmlEscape(firstname))
                .replace("{{token}}", token == null ? "" : HtmlUtils.htmlEscape(token));
    }

    public String resetPasswordEmail(String firstname, String token) {
        String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Your Password</title>
        </head>
        <body style="margin:0;padding:0;background-color:#f6f6f6;font-family:Helvetica,Arial,sans-serif;">
            <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f6f6f6;padding:20px 0;">
                <tr>
                    <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                            <!-- Header -->
                            <tr>
                                <td style="background-color:#dc3545;padding:30px;text-align:center;border-radius:8px 8px 0 0;">
                                    <h1 style="margin:0;color:#ffffff;font-size:28px;font-weight:bold;">BEN & CO</h1>
                                </td>
                            </tr>
        
                            <!-- Body Content -->
                            <tr>
                                <td style="padding:40px 30px;">
                                    <h2 style="margin:0 0 20px 0;color:#0b0c0c;font-size:24px;">Hi {{firstname}},</h2>
                                    <p style="margin:0 0 20px 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                        We received a request to reset your password. Use the verification code below to complete the process:
                                    </p>
        
                                    <!-- Token Box -->
                                    <table width="100%" cellpadding="0" cellspacing="0" style="margin:30px 0;">
                                        <tr>
                                            <td align="center">
                                                <div style="display:inline-block;background-color:#f8f9fa;border:2px dashed #dc3545;border-radius:8px;padding:20px 40px;">
                                                    <span style="font-size:32px;font-weight:bold;color:#dc3545;letter-spacing:5px;font-family:monospace;">{{token}}</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
        
                                    <p style="margin:20px 0 0 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                        This code will expire in 10 minutes for security reasons.
                                    </p>
        
                                    <p style="margin:20px 0 0 0;color:#0b0c0c;font-size:16px;line-height:1.6;">
                                        If you didn't request a password reset, please ignore this email or contact support if you have concerns.
                                    </p>
        
                                    <!-- Security Note -->
                                    <div style="margin-top:30px;padding:15px;background-color:#fff3cd;border-left:4px solid:#ffc107;border-radius:4px;">
                                        <p style="margin:0;color:#856404;font-size:14px;line-height:1.6;">
                                            <strong>Security Tip:</strong> Never share this code with anyone. BEN & CO will never ask for your password or verification code via email or phone.
                                        </p>
                                    </div>
                                </td>
                            </tr>
        
                            <!-- Footer -->
                            <tr>
                                <td style="background-color:#f8f9fa;padding:20px 30px;text-align:center;border-radius:0 0 8px 8px;">
                                    <p style="margin:0;color:#6c757d;font-size:14px;">
                                        © 2026 BEN & CO. All rights reserved.
                                    </p>
                                    <p style="margin:10px 0 0 0;color:#6c757d;font-size:12px;">
                                        This is an automated email. Please do not reply to this message.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;

        return html.replace("{{firstname}}", firstname == null ? "" : firstname)
                .replace("{{token}}", token == null ? "" : token);
    }
}