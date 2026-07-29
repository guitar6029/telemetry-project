import { Component, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { MemberService } from "../../services/member.service";
import { OrganizationMembershipResponse } from "../../../dto/organization-membership-response";

@Component({
    selector: 'app-member-form',
    templateUrl: './member-form.component.html',
    styleUrl: './member-form.component.scss'
})
export class MemberFormComponent implements OnInit {

    member = signal<OrganizationMembershipResponse | null>(null);
    error = signal<string | null>(null);
    editMode = signal<boolean>(false);

    constructor(
        private route: ActivatedRoute,
        private memberService: MemberService
    ) { }


    ngOnInit(): void {
        //get member id
        const membershipId = this.route.snapshot.paramMap.get("membershipId")

        // decide if Create or Edit mode
        if (membershipId) {
            this.editMode.set(true);
            this.loadMembership(membershipId);
        }

    }

    loadMembership(membershipId: string): void {
        this.error.set(null);
        this.memberService.getMember(membershipId).subscribe({
            next: (response) => {
                this.member.set(response.data)
                console.log("User : ", this.member());
            },
            error: (error) => {
                console.error("Failed to load member.", error);
                this.error.set("Unable to load member");
            }
        })
    }

}
